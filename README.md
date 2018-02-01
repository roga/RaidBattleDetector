# RaidBattleDetector

本程式僅為協助提醒是否有團體戰 (Raid Battle) 正在特定道館 (gym) 舉辦，即時資料來自 gymhuntr.com 。

要偵測到的道館放在 run.groovy 的 gymList 裡面，請自行參考 gymhuntr.com 網站新增道館資料。

本程式執行方式為 `groovy run.groovy`

另外可以把 notify.sh 放到 /usr/local/bin/ 底下，可以提供 Mac 系統的桌面通知功能，(當然您也可以自己改造 notify.sh )

![桌面通知](https://i.imgur.com/OJ7hXiw.png "Mac OS X 可以提供桌面通知")
