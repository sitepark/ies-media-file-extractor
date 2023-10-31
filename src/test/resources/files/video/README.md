Create CSV-File:

```bash
mediainfo --Output=$'General;%CompleteName%,\nVideo;%Width%,%Height%\\n' !(*.csv|*.md) > data.csv
```
