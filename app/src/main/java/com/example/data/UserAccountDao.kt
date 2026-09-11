package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
  @Query("SELECT * FROM user_accounts ORDER BY email ASC")
  fun getAllUsersFlow(): Flow<List<UserAccountEntity>>

  @Query("SELECT * FROM user_accounts")
  suspend fun getAllUsers(): List<UserAccountEntity>

  @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
  suspend fun getUserByEmail(email: String): UserAccountEntity?

  @Query("SELECT * FROM user_accounts WHERE isLastActiveUser = 1 LIMIT 1")
  suspend fun getLastActiveUser(): UserAccountEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserAccountEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUsers(users: List<UserAccountEntity>)

  @Update
  suspend fun updateUser(user: UserAccountEntity)

  @Query("UPDATE user_accounts SET isLastActiveUser = CASE WHEN email = :activeEmail THEN 1 ELSE 0 END")
  suspend fun setActiveUser(activeEmail: String)

  @Query("DELETE FROM user_accounts WHERE email = :email")
  suspend fun deleteUser(email: String)
}
