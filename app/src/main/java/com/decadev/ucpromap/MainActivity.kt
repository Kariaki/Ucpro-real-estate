package com.decadev.ucpromap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.decadev.ucpromap.databinding.ActivityMainBinding
import com.decadev.ucpromap.model.ProfileObj
import com.decadev.ucpromap.model.UserDetails
import com.decadev.ucpromap.repository.Repository
import com.decadev.ucpromap.utils.MainViewModelFactory
import com.decadev.ucpromap.viewModel.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var viewModel: MainViewModel



    companion object {
        private const val RC_SIGN_IN = 120
        val TAG = "Sign In Activity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val newsRepository = Repository()
        val viewModelProviderFactory = MainViewModelFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Firebase auth instance
        mAuth = FirebaseAuth.getInstance()

        binding.btnActivity.setOnClickListener {
            val myIntent = Intent(this, MapActivity::class.java)
            startActivity(myIntent)
        }

        binding.googleLogoImageView.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    val userInformations = ProfileObj(account.email!!, account.familyName!!, account.givenName!!, account.photoUrl.toString(), account.displayName!!)
                    viewModel.pushUserDetails(UserDetails(userInformations))
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    /** Google Sign In failed, update UI appropriately **/
                    Snackbar.make(binding.root, "Google sign in failed: $e", Snackbar.LENGTH_LONG).show()
                }
            } else {
                Snackbar.make(binding.root, "Sign in Not Successful $exception", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    /** Sign in success, update UI with the signed-in user's information**/
                    userResponseViewModel()
                } else {
                    /** If sign in fails, display a message to the user.**/
                    Snackbar.make(binding.root, "signInWithCredential:failure: ${task.exception}", Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun userResponseViewModel() {
        viewModel.userResponse.observe(this, Observer { response ->
            if (response.isSuccessful) {
                Snackbar.make(binding.root, response.body()!!.message, Snackbar.LENGTH_LONG).apply {
                    setAction("login to Map") {
                        val intent = Intent(this@MainActivity, MapActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    show()
                }
            } else {
                Snackbar.make(binding.root, "Sign in Not Successful", Snackbar.LENGTH_LONG).show()
            }
        })
    }
}