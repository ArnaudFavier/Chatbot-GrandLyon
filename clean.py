#! /usr/bin/python


import json
from pprint import pprint
import io
try:
    to_unicode = unicode
except NameError:
    to_unicode = str

# json_file='data/POI_touristique.json'
json_file='data/data.json'


json_data=open(json_file)
data = json.load(json_data)

i = 0
while i != len(data['features']) :
	if data['features'][i]['properties']['type'] != 'PATRIMOINE_CULTUREL':
		data['features'].pop(i)
	else :
		if 'id_sitra1' in data['features'][i]['properties'] : 
			del data['features'][i]['properties']['id_sitra1']
		if 'fax' in data['features'][i]['properties'] : 
			del data['features'][i]['properties']['fax']
		if 'telephonefax' in data['features'][i]['properties'] : 	
			del data['features'][i]['properties']['telephonefax']
		if 'gid' in data['features'][i]['properties'] : 	
			del data['features'][i]['properties']['gid']
		if 'date_creation' in data['features'][i]['properties'] : 	
			del data['features'][i]['properties']['date_creation']
		if 'last_update' in data['features'][i]['properties'] : 	
			del data['features'][i]['properties']['last_update']
		if 'last_update_fme' in data['features'][i]['properties'] : 	
			del data['features'][i]['properties']['last_update_fme']
		if 'coordinates' in data['features'][i]['geometry'] :
			coordinates = [0,0]
			coordinates[0] = data['features'][i]['geometry']['coordinates'][1]
			coordinates[1] = data['features'][i]['geometry']['coordinates'][0]
			data['features'][i]['geometry']['coordinates'] = coordinates
		i = i+1
	

	
# with open('data/POI_touristique_filtered.json', 'w') as f:
with open('data/test.json', 'w') as f:
	f.write(json.dumps(data, indent=4, sort_keys=True))
    # json.dump(data, f)


json_data.close()