@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
import groovyx.net.http.RESTClient

gymList = [
        [
                id : '5dee2e8612c24ba29d64a4493103bef3.16',
                name : '永欣里公園 柱狀雕塑',
                latitude : '25.123856',
                longitude : '121.526775'
        ],
        [
                id : 'bdd53b4d3d284d1d8e8c7719acf9dca4.16',
                name : 'Fuxing Xmax Park (福興聖誕公園)',
                latitude : '25.111196',
                longitude : '121.51328'
        ],
        [
                id : 'ad411927d28749dc833d6c4849dd6878.16',
                name : '獅子會時鐘',
                latitude : '25.121386',
                longitude : '121.499351'
        ],
        [
                id : '495431e7be6f405cb3b4d0d0450f3cd3.16',
                name : '中庸二號公園',
                latitude : '25.13723',
                longitude : '121.501283'
        ],
        [
                id : '92f03256f39b4f0f903f46fbc30e290a.16',
                name : '復公綠白涼亭 (復興公園)',
                latitude : '25.139179',
                longitude : '121.503144'
        ],
        [
                id : '3359ee9e8a2a43b891e0c5ab967c9c08.16',
                name : '七星涼亭',
                latitude : '25.136098',
                longitude : '121.501942'
        ],
        [
                id : '72036af021be4964af3599292c663811.16',
                name : '圓形拱門',
                latitude : '25.131961',
                longitude : '121.503578'
        ],
        [
                id : '3c1b5ea677ee4e0d999d3cc1e9dcdc9c.16',
                name : '北投公園博物館',
                latitude : '25.136753',
                longitude : '121.5071'
        ],
        [
                id : 'ca3a4709199540b19333b4d502741de2.16',
                name : '燈光噴水地板',
                latitude : '25.058067',
                longitude : '121.615534'
        ],
        [
                id : 'a01ef38f2a824dee801a174e1a3177ef.16',
                name : '三貿公園',
                latitude : '25.057866',
                longitude : '121.614258'
        ],
        [
                id : '96e8922f5e3b4498a921e078525f2d13.11',
                name : '中研公園 Academia Park',
                latitude : '25.047187',
                longitude : '121.613656'
        ],
]

println "Raid Battle Detector.."

while(true) {

    if(ceaseFire()) {

        notify("Raid Battle", "cease fire.")
        sleep(1000 * 3600 ) // sleep for 1 hr

    } else {

        gymList.each { gym ->

            data = refreshBattle(gym)
            // println(data) // debug

            def message = "[" + currentDateTime() + "] " + gym.name

            if(data.raids.toString().contains(gym.id)) {
                notify("Raid Battle", "Detected." + gym.name)
                File file = new File("./run.log")
                file.append "Raid Battle Detected: " + message + "\n"
                println("$message - Found!")
            } else {
                println("$message - Not Found.")
            }

            sleep(1000 * 3 ) // sleep for 3 secs

        }

        refreshHashCode()

        sleep(1000 * 60 ) // sleep for 1 min

    }
}

def refreshBattle(def gym) {

    long unixTime = System.currentTimeMillis() / 1000L
    long unitTimeUntil = unixTime + 4846

    def parameter = [
            latitude : gym.latitude,
            longitude : gym.longitude,
            hashCheck : '57b34b3eca72eed3178b785dcca4289g4', // magic
            monster : '83jhs', // magic
            time : unixTime,
            timeUntil : unitTimeUntil
    ]

    def resp = poke('get', '/api/gyms/updates', parameter)
    resp.data
}

def refreshHashCode() {
    def resp = poke('get', '/api/status/gym', [hashCheck:'57b34b3eca72eed3178b785dcca4289g4'])
    resp
}

def poke(def method, def path, def parameter) {
    def client = new RESTClient("https://api.gymhuntr.com")
    client.setHeaders('User-Agent' : 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36')

    def query = [path: path, query: parameter,]
    client."${method}"(query)
}

def currentDateTime() {
    def now = new Date()
    now.format("yyyy/MM/dd HH:mm:ss", TimeZone.getTimeZone('GMT+8:00')).toString()
}

def ceaseFire() {
    def hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    def peaceHours = [0, 1, 2, 3, 4, 5, 6, 7, 8, 19, 20, 21, 22, 23]
    peaceHours.contains(hour);
}

//def reminder() {
//    if(Calendar.getInstance().get(Calendar.MINUTE) % 10 == 0) {
//        notify("Raid Battle", "Still running in background.")
//    }
//}

def notify(def title, def body) {

    def path = "/usr/local/bin/notify.sh"
    def file = new File(path)

    if(file.exists()) {
        def command = [path, title, body]
        def proc = command.execute()
        proc.waitFor()
    }
}
