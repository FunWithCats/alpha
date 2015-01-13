#!/usr/bin/python3

import sys

fichier = open(sys.argv[1],"rb")
tableau = bytearray(fichier.read())
fichier.close()

sortie = open(sys.argv[1] + ".uasm","w")

sortie.write("| Instructions du fichier {0} sous forme d'octets\n\n".format(sys.argv[1]))

i = 0
while (i < len(tableau)):
  octet1 = tableau[i    ]
  octet2 = tableau[i + 1]
  octet3 = tableau[i + 2]
  octet4 = tableau[i + 3]
  
  entier = octet1 + (octet2 << 8) + (octet3 << 16) + (octet4 << 24)
  sortie.write("| Instruction %d, adresse 0x%08X\n| dont l'entier est 0x%08X\n 0x%02X\t0x%02X\t0x%02X\t0x%02X\n\n" % (i/4, i, entier, octet1,octet2, octet3, octet4))
  i = i + 4
  
sortie.close()