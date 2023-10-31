Create CSV-File:

```bash
mediainfo --Output=$'General;%CompleteName%,\nAudio;%Format%\\n' !(*.csv|*.md) > data.csv
```
