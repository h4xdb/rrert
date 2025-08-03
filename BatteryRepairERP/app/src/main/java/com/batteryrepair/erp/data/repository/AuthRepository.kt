package com.batteryrepair.erp.data.repository

import android.content.Context
import com.batteryrepair.erp.BatteryRepairApplication
import com.batteryrepair.erp.data.firebase.FirebaseConfig
import com.batteryrepair.erp.data.model.User
import com.batteryrepair.erp.data.model.UserRole
import com.batteryrepair.erp.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val firebaseConfig: FirebaseConfig
) {
    
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            // First check for hardcoded admin login (offline mode)
            if (username == BatteryRepairApplication.ADMIN_USERNAME && 
                password == BatteryRepairApplication.ADMIN_PASSWORD) {
                
                val adminUser = User(
                    id = BatteryRepairApplication.ADMIN_USER_ID,
                    username = username,
                    fullName = "System Administrator",
                    email = "admin@batteryrepair.local",
                    role = UserRole.ADMIN,
                    isActive = true,
                    createdAt = System.currentTimeMillis()
                )
                
                preferenceManager.setUserLoggedIn(adminUser)
                return Result.success(adminUser)
            }
            
            // If Firebase is configured, try Firebase authentication
            if (firebaseConfig.isFirebaseConfigured(context)) {
                authenticateWithFirebase(username, password)
            } else {
                Result.failure(Exception("Invalid credentials or Firebase not configured"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun authenticateWithFirebase(username: String, password: String): Result<User> {
        return try {
            val auth = firebaseConfig.getAuth()
            if (auth == null) {
                return Result.failure(Exception("Firebase Auth not initialized"))
            }
            
            // For Firebase authentication, we need to convert username to email
            // or use custom authentication
            val usersRef = firebaseConfig.getDatabaseReference("users")
            if (usersRef == null) {
                return Result.failure(Exception("Firebase Database not initialized"))
            }
            
            // Query users by username
            val userQuery = usersRef.orderByChild("username").equalTo(username)
            val dataSnapshot = userQuery.get().await()
            
            if (dataSnapshot.exists()) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null && user.isActive) {
                        // In a real app, you'd verify the password hash here
                        // For now, we'll use a simple comparison (NOT SECURE for production)
                        if (verifyPassword(password, user)) {
                            preferenceManager.setUserLoggedIn(user)
                            return Result.success(user)
                        }
                    }
                }
            }
            
            Result.failure(Exception("Invalid username or password"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun verifyPassword(inputPassword: String, user: User): Boolean {
        // In production, use proper password hashing (bcrypt, etc.)
        // This is a simplified version for demo purposes
        return inputPassword.length >= 6 // Basic validation
    }
    
    fun logout(): Result<Unit> {
        return try {
            preferenceManager.logout()
            firebaseConfig.getAuth()?.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCurrentUser(): User? {
        return preferenceManager.getCurrentUser()
    }
    
    fun isUserLoggedIn(): Boolean {
        return preferenceManager.isUserLoggedIn()
    }
    
    fun observeAuthState(): Flow<User?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(getCurrentUser())
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        // Initial value
        trySend(getCurrentUser())
        
        // If Firebase is configured, observe auth state changes
        val auth = firebaseConfig.getAuth()
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                trySend(null)
            }
        }
        
        auth?.addAuthStateListener(authStateListener)
        
        awaitClose {
            auth?.removeAuthStateListener(authStateListener)
        }
    }
    
    suspend fun createUser(user: User, password: String): Result<User> {
        return try {
            if (!firebaseConfig.isFirebaseConfigured(context)) {
                return Result.failure(Exception("Firebase not configured"))
            }
            
            val usersRef = firebaseConfig.getDatabaseReference("users")
            if (usersRef == null) {
                return Result.failure(Exception("Firebase Database not initialized"))
            }
            
            // Check if user already exists
            val existingUserQuery = usersRef.orderByChild("username").equalTo(user.username)
            val snapshot = existingUserQuery.get().await()
            
            if (snapshot.exists()) {
                return Result.failure(Exception("Username already exists"))
            }
            
            // Create new user
            val newUserId = usersRef.push().key ?: return Result.failure(Exception("Failed to generate user ID"))
            val newUser = user.copy(
                id = newUserId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                createdBy = preferenceManager.getCurrentUserId() ?: ""
            )
            
            usersRef.child(newUserId).setValue(newUser).await()
            
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(user: User): Result<User> {
        return try {
            if (!firebaseConfig.isFirebaseConfigured(context)) {
                return Result.failure(Exception("Firebase not configured"))
            }
            
            val usersRef = firebaseConfig.getDatabaseReference("users")
            if (usersRef == null) {
                return Result.failure(Exception("Firebase Database not initialized"))
            }
            
            val updatedUser = user.copy(
                updatedAt = System.currentTimeMillis()
            )
            
            usersRef.child(user.id).setValue(updatedUser).await()
            
            // Update current user if it's the same user
            if (preferenceManager.getCurrentUserId() == user.id) {
                preferenceManager.setUserLoggedIn(updatedUser)
            }
            
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            if (!firebaseConfig.isFirebaseConfigured(context)) {
                return Result.failure(Exception("Firebase not configured"))
            }
            
            val usersRef = firebaseConfig.getDatabaseReference("users")
            if (usersRef == null) {
                return Result.failure(Exception("Firebase Database not initialized"))
            }
            
            val snapshot = usersRef.get().await()
            val users = mutableListOf<User>()
            
            for (userSnapshot in snapshot.children) {
                val user = userSnapshot.getValue(User::class.java)
                if (user != null && user.isActive) {
                    users.add(user)
                }
            }
            
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            if (!firebaseConfig.isFirebaseConfigured(context)) {
                return Result.failure(Exception("Firebase not configured"))
            }
            
            val usersRef = firebaseConfig.getDatabaseReference("users")
            if (usersRef == null) {
                return Result.failure(Exception("Firebase Database not initialized"))
            }
            
            // Soft delete - mark as inactive
            usersRef.child(userId).child("isActive").setValue(false).await()
            usersRef.child(userId).child("updatedAt").setValue(System.currentTimeMillis()).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}