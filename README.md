# Speaker

원본님의 EconomyAPI를 사용하고 있어야합니다.

#settings.json
```
{
  "default-chat": "world",
  "default-chat-op": "server"
  "speaker-cost": "10000",
  "speaker-pattern": "§b[확성기] {NAME} : {MESSAGE}"
}
```

default-chat : world 또는 server
기본 채팅이 어디로 나갈지

default-chat-op : world 또는 server
오피의 기본 채팅이 어디로 갈지

speaker-cost : 스피커를 사용하면 드는 비용을 적습니다.0보다 작으면 자동으로 10000으로 변경됨

speaker-pattern : 스피커를 사용했을때 뜨는 모양을 적습니다.

{NAME} : 플레이어 이름
{MESSAGE} : 메세지 내용
