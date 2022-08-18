import com.github.hank9999.kook.Bot
import com.github.hank9999.kook.Config
import com.github.hank9999.kook.handler.types.FilterTypes
import com.github.hank9999.kook.http.Api
import com.github.hank9999.kook.http.HttpApi
import com.github.hank9999.kook.types.Event
import com.github.hank9999.kook.types.Message
import kotlinx.coroutines.*
import kotlinx.serialization.json.jsonArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("main")

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val bot = Bot(Config(token = "xxxx"))
    // 注册监听事件
    bot.registerClass(M())
    bot.registerMessageFunc { msg, cs -> message2(msg, cs) }
    bot.registerMessageFunc { msg, cs -> message3(msg, cs) }
    GlobalScope.launch {
        // 调用未封装 API 返回 JSON 对象, 需要在协程里调用
        // 获取第一个已加入服务器
        val data1 = HttpApi.request(Api.Guild.List())
        print(data1.jsonArray[0])
        // 获取每一页第一个已加入服务器
        HttpApi.requestAsFlow(Api.Guild.List()).collect {
            print(it.jsonArray[0])
        }
        HttpApi.requestAsIterator(Api.Guild.List()).forEach {
            println(it.jsonArray[0])
        }
        // 调用已封装 API, 需要在协程里调用
        // 获取网关
        println(HttpApi.Gateway.index()) // 直接是 URL
    }
}

class M {
    @Bot.OnMessage
    suspend fun message(msg: Message) {
        logger.info(msg.toString())
    }

    @Bot.OnEvent
    suspend fun event(event: Event) {
        logger.info(event.toString())
    }

    @Bot.OnFilter(FilterTypes.START_WITH, "/1234")
    suspend fun filter(msg: Message) {
        logger.info(msg.toString())
        msg.reply("1234") // 快捷回复
    }

    @Bot.OnCommand("test")
    suspend fun command(msg: Message) {
        logger.info(msg.toString())
        msg.send("1234") // 快速发送
    }
}
fun message2(msg: Message, cs: CoroutineScope) {
    println(msg)
}

fun message3(msg: Message, cs: CoroutineScope) {
    cs.launch {
        delay(500)
        logger.info(msg.toString())
    }
}