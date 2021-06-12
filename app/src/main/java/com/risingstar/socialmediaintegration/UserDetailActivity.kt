package com.risingstar.socialmediaintegration

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterCore


class UserDetailActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var txtUser : TextView
    lateinit var btnSignOut : Button
    lateinit var imgUser : ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        val buttonType = intent.getStringExtra("Button")

        txtUser = findViewById(R.id.txtUser)
        imgUser = findViewById(R.id.imgUser)
        btnSignOut = findViewById(R.id.btn_sign_out)

        when(buttonType){
            "google"->{
                val bundleArgs = intent.getBundleExtra("bundle")
                val userName = bundleArgs?.getString("User name")
                val userPhotoUrl = bundleArgs?.getString("User photo url")
                val userEmail = bundleArgs?.getString("User Mail")



                txtUser.text = "$userName \n$userEmail"
                Picasso.get().load(userPhotoUrl).error(R.drawable.ic_profile).into(imgUser)


                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()

                mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

                btnSignOut.text = "Sign out"
                btnSignOut.setOnClickListener {
                    signOut()
                }
            }
            "twitter"->{
                val username = intent.getStringExtra("username")
                txtUser.text = "User Name : $username"
                btnSignOut.text = "Log out"
                btnSignOut.setOnClickListener {
                    logOut()
                }
            }

        }

    }
    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                Toast.makeText(this,"Sign out Successful !!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext,MainActivity :: class.java))
                this@UserDetailActivity.finish()
            })
    }
    private fun logOut(){
        CookieSyncManager.createInstance(this)
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.removeSessionCookie()
        TwitterCore.getInstance().getSessionManager().clearActiveSession()
        Toast.makeText(this,"Logged out Successful !!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(applicationContext,MainActivity :: class.java))
        this@UserDetailActivity.finish()
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this)
    }

}