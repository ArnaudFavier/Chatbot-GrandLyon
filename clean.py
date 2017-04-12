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

i = 0
while i != len(data['features']) :
	del data['features'][i]['properties']['id_sitra1']
	del data['features'][i]['properties']['fax']
	del data['features'][i]['properties']['telephonefax']
	del data['features'][i]['properties']['gid']
	del data['features'][i]['properties']['date_creation']
	del data['features'][i]['properties']['last_update']
	del data['features'][i]['properties']['last_update_fme']
	if data['features'][i]['properties']['type'] != 'PATRIMOINE_CULTUREL':
		data['features'].pop(i)
	else :
		i = i+1

with open('data/POI_touristique_filtered.json', 'w') as f:
	f.write(json.dumps(data, indent=4, sort_keys=True))
    # json.dump(data, f)


json_data.close()