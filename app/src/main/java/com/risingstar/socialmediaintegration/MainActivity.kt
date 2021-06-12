package com.risingstar.socialmediaintegration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton


class MainActivity : AppCompatActivity()  {

    lateinit var googleSignInButton: SignInButton
    lateinit var mGoogleSignInClient : GoogleSignInClient
    private val RC_SIGN_IN = 10

    lateinit var twitterLoginButton : TwitterLoginButton
    var twitterLoggedIn : Boolean = false

    var token : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Twitter.initialize(this@MainActivity)
        setContentView(R.layout.activity_main)

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)

        // Set the dimensions of the sign-in button.
        // Set the dimensions of the sign-in button.
        googleSignInButton = findViewById(R.id.google_sign_in_button)
        googleSignInButton.setSize(SignInButton.SIZE_WIDE)
        twitterLoginButton = findViewById(R.id.twitter_login_button)

        googleSignInButton.setOnClickListener {
            googleSignIn()
        }

        twitterLoginButton.callback = object : Callback<TwitterSession?>() {
            override fun success(result: Result<TwitterSession?>?) {
                // Do something with result, which provides a TwitterSession for making API calls
                val session = TwitterCore.getInstance().sessionManager.activeSession
                val authToken = session.authToken
                //String token = authToken.token;
                //  String secret = authToken.secret;
                twitterLoggedIn = true
                loginMethod(session)
            }

            override fun failure(exception: TwitterException) {
                // Do something on failure
                Toast.makeText(applicationContext, "Login fail", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }


        // Pass the activity result to the login button.
        twitterLoginButton.onActivityResult(requestCode, resultCode, data)

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto: Uri? = acct.photoUrl

                val bundle = Bundle()
                bundle.putString("User name",personName)
                bundle.putString("User photo url",personPhoto.toString())
                bundle.putString("User Mail",personEmail)

                val intent = Intent(this,UserDetailActivity::class.java)
                intent.putExtra("Button","google")
                intent.putExtra("bundle",bundle)
                Toast.makeText(this,"Login Successful !!", Toast.LENGTH_LONG).show()
                startActivity(intent)

            }
            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("Message", e.statusCode.toString())
        }
    }

    override fun onStart() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val session = TwitterCore.getInstance().sessionManager.activeSession

        if(account!=null){
            val personName = account.displayName
            val personGivenName = account.givenName
            val personFamilyName = account.familyName
            val personEmail = account.email
            val personId = account.id
            val personPhoto: Uri? = account.photoUrl

            val bundle = Bundle()
            bundle.putString("User Id",personId)
            bundle.putString("User name",personName)
            bundle.putString("User photo url",personPhoto.toString())
            bundle.putString("User Mail",personEmail)

            val intent = Intent(this,UserDetailActivity::class.java)
            intent.putExtra("Button","google")
            intent.putExtra("bundle",bundle)
            Toast.makeText(this,"Login Successful !!",Toast.LENGTH_LONG).show()
            startActivity(intent)
            this@MainActivity.finish()
        }

        if (twitterLoggedIn){
            loginMethod(session)
        }

        super.onStart()
    }




    fun loginMethod(twitterSession: TwitterSession) {
        val userName = twitterSession.userName
        val intent = Intent(this@MainActivity, UserDetailActivity::class.java)
        intent.putExtra("Button","twitter")
        intent.putExtra("username", userName)
        startActivity(intent)
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this)
    }




}










//40:54:28:42:4B:A5:21:8E:5D:AF:2C:0B:78:7D:1E:10:0F:05:A3:A2