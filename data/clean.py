#! /usr/bin/python

import json
import io
import sys
import os


# Test les arguments : il faut au moins donner un fichier.json
isFilter = False
if len(sys.argv) > 3 or len(sys.argv) < 2:
	print("Error : mauvais nombre d'argument ! Exemple : ")
	print("python clean.py ./data.json ./filter.json")
	sys.exit()
else:
	if len(sys.argv) == 3:
		isFilter = True

# Ouverture du fichier contenant les donnees
try :
	json_file = sys.argv[1]
	json_data = open(json_file)
	data = json.load(json_data)
except ValueError :
	print("Fichier " + sys.argv[1] + " mal forme !")
	sys.exit()
except IOError :
	print("Error lors de l'ouverture du fichier " + sys.argv[1] + ". Fichier innexistant !") 
	sys.exit()

# Ouverture du fichier contenant les filtres si besoin
if isFilter == True : 
	try:
		json_data_filter = open(sys.argv[2])
		data_filter = json.load(json_data_filter)
	except ValueError :
		print("Fichier " + sys.argv[2] + " mal forme !")
		sys.exit()
	except IOError :
		print("Error lors de l'ouverture du fichier " + sys.argv[2] + ". Fichier innexistant !") 
		sys.exit()
else :
	data_filter = json.loads(json.dumps({"deleteProperties": [],	"filters" : {}})) 

i = 0
# Parcourt de chaque tuple du fichier Json
while i != len(data['features']) :
	keepIt = True
	# Application des filtres
	if 'filters' in data_filter :
		for f in data_filter['filters'] :
			if f not in data['features'][i]['properties'] : 
				keepIt = False
				break
			if type(data_filter['filters'][f]) is list :
				test = False
				for f2 in data_filter['filters'][f] :
					if data['features'][i]['properties'][f] == f2 : 
						test = True
						break
				keepIt = test
			else :
				if data['features'][i]['properties'][f] != data_filter['filters'][f] :
					keepIt = False
			if keepIt == False :
				break
	# Suppression de l'element car il ne correspond pas aux filtres
	if keepIt == False :
		data['features'].pop(i)
	else :
		# Suppression des attributs donnes dans le filtre
		if 'deleteProperties' in data_filter :
			for p in data_filter['deleteProperties'] :
				if p in data['features'][i]['properties'] : 
					del data['features'][i]['properties'][p]
		# Inversion des coordonnees GPS pour les mettre dans l'ordre Google Maps
		if 'coordinates' in data['features'][i]['geometry'] :
			coordinates = [0,0]
			coordinates[0] = data['features'][i]['geometry']['coordinates'][1]
			coordinates[1] = data['features'][i]['geometry']['coordinates'][0]
			data['features'][i]['geometry']['coordinates'] = coordinates
		i = i+1

data['doc_id'] = os.path.splitext(os.path.basename(json_file))[0] + '_filtered'

# Ecriture du resultat
with open(os.path.splitext(json_file)[0]+'_filtered.json', 'w') as f:
	f.write(json.dumps(data, indent=4, sort_keys=True))

if isFilter == True :
	json_data_filter.close()

json_data.close()











