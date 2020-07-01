package huawei.messaging

import android.content.Intent
import android.os.IBinder
import com.huawei.agconnect.credential.obs.s
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import org.appcelerator.kroll.common.Log
import org.appcelerator.titanium.util.TiPlatformHelper.TAG
import java.util.*


class MessagingService : HmsMessageService() {

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        Log.i(TAG, "onMessageReceived is called")
        if (message == null) {
            Log.e(TAG, "Received message entity is null!")
            return
        }
        Log.i(TAG, "getCollapseKey: " + message.collapseKey
                .toString() + "\n getData: " + message.data
                .toString() + "\n getFrom: " + message.from
                .toString() + "\n getTo: " + message.to
                .toString() + "\n getMessageId: " + message.messageId
                .toString() + "\n getOriginalPriority: " + message.originalPriority
                .toString() + "\n getPriority: " + message.priority
                .toString() + "\n getSendTime: " + message.sentTime
                .toString() + "\n getMessageType: " + message.messageType
                .toString() + "\n getTtl: " + message.ttl)
        val notification: RemoteMessage.Notification = message.notification
        if (notification != null) {
            Log.i(TAG, """
                 getImageUrl: ${notification.imageUrl}
                 getTitle: ${notification.title}
                 getTitleLocalizationKey: ${notification.titleLocalizationKey}
                 getTitleLocalizationArgs: ${Arrays.toString(notification.titleLocalizationArgs)}
                 getBody: ${notification.body}
                 getBodyLocalizationKey: ${notification.bodyLocalizationKey}
                 getBodyLocalizationArgs: ${Arrays.toString(notification.bodyLocalizationArgs)}
                 getIcon: ${notification.icon}
                 getSound: ${notification.sound}
                 getTag: ${notification.tag}
                 getColor: ${notification.color}
                 getClickAction: ${notification.clickAction}
                 getChannelId: ${notification.channelId}
                 getLink: ${notification.link}
                 getNotifyId: ${notification.notifyId}""")
        }
    }

    override fun onNewToken(token: String) {
        Log.i(TAG, "received refresh token:$token")

        val module: TitaniumHuaweiMessagingModule = TitaniumHuaweiMessagingModule.getInstance()!!
        if (module != null) {
            module.onTokenRefresh(token)
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()

        // This method is currently not implemented by Huawei
    }
}