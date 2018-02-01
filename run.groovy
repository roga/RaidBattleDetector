@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
import groovyx.net.http.RESTClient

gymList = [
        gym1 : [
                id : '5dee2e8612c24ba29d64a4493103bef3.16',
                name : '永欣里公園 柱狀雕塑',
                latitude : '25.123856',
                longitude : '121.526775'
        ],
        gym2 : [
                id : 'bdd53b4d3d284d1d8e8c7719acf9dca4.16',
                name : 'Fuxing Xmax Park (福興聖誕公園)',
                latitude : '25.111196',
                longitude : '121.51328'
        ],
        gym3 : [
                id : '96e8922f5e3b4498a921e078525f2d13.11',
                name : '中研公園 Academia Park',
                latitude : '25.047187',
                longitude : '121.613656'
        ],
        gym4 : [
                id : '495431e7be6f405cb3b4d0d0450f3cd3.16',
                name : '中庸二號公園',
                latitude : '25.13723',
                longitude : '121.501283'
        ],
        gym5 : [
                id : '3359ee9e8a2a43b891e0c5ab967c9c08.16',
                name : '七星涼亭',
                latitude : '25.136098',
                longitude : '121.501942'
        ]
]

println "Raid Battle Detector.."

while(true) {

    if(ceaseFire()) {

        notify("Raid Battle", "cease fire.")
        sleep(1000 * 3600 ) // sleep for 1 hr

    } else {

        gymList.each { key, gym ->

            data = refreshBattle(gym)
            // println(data) // debug

            def message = "[" + currentDateTime() + "] " + gym.name

            if(data.raids.toString().contains(gym.id)) {

                notify("Raid Battle", "Detected." + gym.name)

                println("$message - Found!")
                File file = new File("./run.log")
                file.append "Raid Battle Detected: " + message + "\n"
            } else {
                println("$message - Not Found.")
                if(isNotify()) {
                    notify("Raid Battle", "Still running in background...")
                }
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
    now.format("yyyy/MM/dd HH:mm:ss", TimeZone.getTimeZone('CST')).toString()
}

def ceaseFire() {
    def hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    def peaceHours = [0, 1, 2, 3, 4, 5, 6, 7, 8, 19, 20, 21, 22, 23]
    peaceHours.contains(hour);
}

def isNotify() {
    Calendar.getInstance().get(Calendar.MINUTE) % 10 == 0
}

/**
 * put the script to /usr/local/bin/notify.sh and chmod +x
 *
 *      #!/bin/sh
 *      /usr/bin/osascript -e "display notification \"$2\" with title \"$1\""
 *
 */

def notify(def title, def body) {
    def command = ['/usr/local/bin/notify.sh', title, body]
    def proc = command.execute()
    proc.waitFor()
}