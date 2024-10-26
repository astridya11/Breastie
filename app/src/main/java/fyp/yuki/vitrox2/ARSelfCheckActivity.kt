package fyp.yuki.vitrox2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ARSelfCheckActivity: AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar);

        findViewById<Button>(R.id.startButton).setOnClickListener {
            val intent = Intent(this@ARSelfCheckActivity, CameraXLivePreviewActivity::class.java)
            startActivity(intent)
        }

        if (!allRuntimePermissionsGranted()) {
            getRuntimePermissions()
        }

        val homeBt = findViewById<LinearLayout>(R.id.home)
        val bmiBt = findViewById<LinearLayout>(R.id.bmi_icon)
        val ARBt = findViewById<LinearLayout>(R.id.AR)
        val grpchatBt = findViewById<LinearLayout>(R.id.chatBot)
        val userBt = findViewById<LinearLayout>(R.id.user_icon)
        val communityBt = findViewById<LinearLayout>(R.id.community)


        // user profile
        userBt.setOnClickListener {
            val intent = Intent(applicationContext, UserProfile::class.java)
            startActivity(intent)
            finish()
        }

        // chat
        grpchatBt.setOnClickListener {
            val intent = Intent(applicationContext, GroupListPage::class.java)
            startActivity(intent)
            finish()
        }

        // community
        communityBt.setOnClickListener {
            val intent = Intent(applicationContext, Community::class.java)
            startActivity(intent)
            finish()
        }


        // main activity
        homeBt.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        // bmi calculator
        bmiBt.setOnClickListener {
            val intent = Intent(applicationContext, bmi::class.java)
            startActivity(intent)
            finish()
        }


        // AR self examination
        ARBt.setOnClickListener {
            val intent = Intent(applicationContext, ARSelfCheckActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun allRuntimePermissionsGranted(): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val permissionsToRequest = ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toTypedArray(),
                    PERMISSION_REQUESTS
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    companion object {
        private const val TAG = "ARSelfCheckActivity"
        private const val PERMISSION_REQUESTS = 1

        private val REQUIRED_RUNTIME_PERMISSIONS =
                arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
    }
}