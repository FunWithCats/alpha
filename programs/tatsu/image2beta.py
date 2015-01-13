#!/usr/bin/python2
from PIL import Image

image   = Image.open("./image.png")
im      = image.transpose(Image.ROTATE_180)
im.convert("RGBA")
largeur, hauteur = im.size
# Liste des pixels, dans le meme ordre que dans Alpha
# Mais au format (R, V, B, A) avec 0 <= R,V,B <= 255 (A = canal alpha, transparence)
pixels           = list(im.getdata())


print ("ImageLargeur = %d" % largeur)
print ("ImageHauteur = %d" % hauteur)
print ("ImageTailleEnEntiers = %d" % ((largeur * hauteur) / 2))
print ("Image:")
       

while(len(pixels) >= 2):
  # On lit les pixels deux par deux
  # Pixel gauche, bits de poids faibles
  (rl,gl,bl,al) = pixels.pop()
  
  # Pixel droit, bits de poid fort
  (rr,gr,br,ar) = pixels.pop()
  
  rl = rl >> 4
  gl = gl >> 4
  bl = bl >> 4
  
  gauche = bl | (gl << 4) | (rl << 8)
  
  rr = rr >> 4
  gr = gr >> 4
  br = br >> 4
  
  droit = br | (gr << 4) | (rr << 8)
  
  gaucheEtDroit = gauche | (droit << 16)
  
  #On sauvegarde l'image
  print ("\t\tLONG(0x%x)" % gaucheEtDroit)