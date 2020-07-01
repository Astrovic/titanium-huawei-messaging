package huawei.messaging

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

class PushHandlerActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
//        try {
//            super.onCreate(savedInstanceState)
//            finish()
//            val module: TitaniumHuaweiMessagingModule = TitaniumHuaweiMessagingModule.getInstance()
//            val context = applicationContext
//            val notification = intent.getStringExtra("fcm_data")
//            if (module != null) {
//                module.setNotificationData(notification)
//            }
//            val launcherIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
//            launcherIntent!!.addCategory(Intent.CATEGORY_LAUNCHER)
//            launcherIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            launcherIntent.putExtra("fcm_data", notification)
//            startActivity(launcherIntent)
//        } catch (e: Exception) {
//            // noop
//        } finally {
//            finish()
//        }
    }

    override fun onResume() {
        Log.d(LCAT, "resumed")
        super.onResume()
    }

    companion object {
        private const val LCAT = "FirebaseCloudMessaging"
    }
}