#!/usr/bin/python3

import sys

fichier = open(sys.argv[1],"rb")
tableau = bytearray(fichier.read())
fichier.close()

sortie = open(sys.argv[1] + ".uasm","w")

sortie.write("| Instructions du fichier {0} sous forme d'entiers\n\n".format(sys.argv[1]))
sortie.write("| Ecriture des entiers 32 bits en little-endian\n")
sortie.write(".macro LONG(x) x%0x100 (x>>8)%0x100 (x>>16)%0x100 (x>>24)%0x100\n\n")


i = 0
while (i < len(tableau)):
  octet1 = tableau[i    ]
  octet2 = tableau[i + 1]
  octet3 = tableau[i + 2]
  octet4 = tableau[i + 3]
  
  entier = octet1 + (octet2 << 8) + (octet3 << 16) + (octet4 << 24)
  sortie.write("| Instruction %d, adresse 0x%08X\n\tLONG(0x%08X)\n\n" % (i/4, i , entier))
  i = i + 4

sortie.close()

		 
  
