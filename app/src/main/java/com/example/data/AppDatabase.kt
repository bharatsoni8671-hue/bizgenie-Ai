package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessProfileDao {
    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    fun getProfileFlow(): Flow<BusinessProfile?>

    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): BusinessProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: BusinessProfile)

    @Query("DELETE FROM business_profile")
    suspend fun clear()
}

@Dao
interface GeneratedWebsiteDao {
    @Query("SELECT * FROM generated_website ORDER BY id DESC")
    fun getAllWebsitesFlow(): Flow<List<GeneratedWebsite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(website: GeneratedWebsite)

    @Query("DELETE FROM generated_website")
    suspend fun clear()
}

@Dao
interface MarketingPostDao {
    @Query("SELECT * FROM marketing_post ORDER BY id DESC")
    fun getAllPostsFlow(): Flow<List<MarketingPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: MarketingPost)

    @Query("DELETE FROM marketing_post")
    suspend fun clear()
}

@Dao
interface BrandIdentityDao {
    @Query("SELECT * FROM brand_identity ORDER BY id DESC")
    fun getAllIdentitiesFlow(): Flow<List<BrandIdentity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(identity: BrandIdentity)

    @Query("DELETE FROM brand_identity")
    suspend fun clear()
}

@Database(
    entities = [
        BusinessProfile::class,
        GeneratedWebsite::class,
        MarketingPost::class,
        BrandIdentity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessProfileDao(): BusinessProfileDao
    abstract fun generatedWebsiteDao(): GeneratedWebsiteDao
    abstract fun marketingPostDao(): MarketingPostDao
    abstract fun brandIdentityDao(): BrandIdentityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bizgenie_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
