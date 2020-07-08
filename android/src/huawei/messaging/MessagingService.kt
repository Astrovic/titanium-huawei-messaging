package huawei.messaging

import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import org.appcelerator.kroll.common.Log
import java.util.Arrays

class MessagingService : HmsMessageService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)

        Log.i("HCM", "onMessageReceived is called")

        if (message == null) {
            Log.e("HCM", "Received message entity is null!")
            return
        }

        Log.i("HCM", "getCollapseKey: " + message.collapseKey
                .toString() + "\n getData: " + message.data
                .toString() + "\n getFrom: " + message.from
                .toString() + "\n getTo: " + message.to
                .toString() + "\n getMessageId: " + message.messageId
                .toString() + "\n getSendTime: " + message.sentTime
                .toString() + "\n getMessageType: " + message.messageType
                .toString() + "\n getTtl: " + message.ttl)

        val notification: RemoteMessage.Notification = message.notification

        Log.i("HCM", """
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

        TitaniumHuaweiMessagingModule.getInstance().let { module ->
            module?.onMessageReceived(message)
        }
    }

    override fun onNewToken(token: String) {
        Log.i("HCM", "received refresh token:$token")

        TitaniumHuaweiMessagingModule.getInstance().let { module ->
            module?.onTokenRefresh(token)
        }
    }
}