#! /usr/bin/python


import json
from pprint import pprint
import io
try:
    to_unicode = unicode
except NameError:
    to_unicode = str

json_file='data/POI_touristique.json'

json_data=open(json_file)
data = json.load(json_data)
#pprint(data)

# for f in data['features']: 
# 	if f['properties']['type'] != 'DEGUSTATION' :
		#print('property.type: ' + f['properties']['type'])
		

out = data
i = 0
while i != len(data['features']) :
	if data['features'][i]['properties']['type'] != 'PATRIMOINE_CULTUREL':
		# print(data['features'][i]['properties']['type'])
		data['features'].pop(i)
	else :
		i = i+1

with open('data/POI_touristisque_filtered.json', 'w') as f:
     json.dump(data, f)


json_data.close()
