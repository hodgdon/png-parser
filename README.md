# png-parser
Evaluate [kotlin multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html) with sample code which reads the [PNG image format](https://en.wikipedia.org/wiki/Portable_Network_Graphics).

To generate test data, use imagemagick.  Example: 1x1 white colored pixel created using [imagemagick](https://imagemagick.org/):
```
convert -size 1x1 xc:white white.png
xxd -p white.png
```