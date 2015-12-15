import webbrowser
from cassandra.cluster import Cluster
import json
import subprocess
import webbrowser
import sys
from preprocess import process_term

def connectToCluster():
    KEYSPACE = "big_data_final"
    cluster = Cluster(protocol_version=2)
    session = cluster.connect(KEYSPACE)
    return session

def getDocuments(term,session):
    query = "select document FROM doc_word_map where word = '%s' ;"%term
    rows = session.execute(query)
    return [row.document for row in rows]

def getWordsAndTFIDF(document,session):
    query = "select word, tfidf from doc_word_map where document = '%s';"%document
    rows = session.execute(query)
    return [(row.word,row.tfidf) for row in rows]

def generate_json(term,session, words_per_layer = 5,parent_docs = [],level = 1,layers = 2,seen_set = set()):
	'''Recursively creates the Json as specified by the user'''
	print term
	if level == 0:
		process_term(term)
	docs = getDocuments(term,session)
	if len(docs) == 0:
	     raise ValueError('Input argument is a stopword or does not exist on the corpus')
	occ_map = {}
	#only get documents where they co-occur, so each level always co-occurs with previous ones
	if len(parent_docs) > 0:
	    docs = list(set(docs) & set(parent_docs))
	#add word to seen_set
	seen_set.add(term)
	research_stopwords = set(["et","al","fig","nm","pdb","imag"])
	for doc in docs:
	    word_tfidf =  getWordsAndTFIDF(doc,session)
	    for w in word_tfidf:
	        if w[0] in seen_set or w[0] in research_stopwords:
	            continue
	        if not w[0] in occ_map:
	            occ_map[w[0]] = 0.0
	        occ_map[w[0]] += w[1]
	    
	order = sorted(occ_map, key= occ_map.get)[::-1]
	flare_dict = {}
	flare_dict["name"] = term
	flare_dict["children"] = []

	for w in order[:words_per_layer]:
	    
	    if level == layers:
	        level_dict = {}
	        level_dict["name"] = w
	        level_dict["size"] = occ_map[w]
	        flare_dict["children"].append(level_dict)
	    else:
	        level_dict = generate_json(w,session,words_per_layer = words_per_layer,parent_docs = docs,level = level+1,layers = layers,seen_set = seen_set)
	        level_dict["size"] = occ_map[w]
	        flare_dict["children"].append(level_dict)
	#after full chain related to word has been computed, remove word from seen_set
	seen_set.remove(term)
	if level == 1:
	    with open('../json_files/flare.json', 'w') as outfile:
	        json.dump(flare_dict, outfile)

	else:
	    return flare_dict

def open_html(f = "final_result_circular.html"):
	url = 'http://127.0.0.1:8080/html_final/'+f
	if sys.platform == 'darwin':    # in case of OS X
 		subprocess.Popen(['open', url])
	else:
		webbrowser.open_new_tab(url)
def get_user_input():
	while True:

	    try:
	        term = raw_input("Please input the term you want to visualize: ")
	        if term == "":
	            break
	            
	        words_per_layer = input("Please input the numbers of co-occurrences per term (0 for default): ")
	        layers = input("Please input the numbers of layers for the visualization(0 for default): ")
	        if words_per_layer <= 0:
	              words_per_layer = 5
	        if layers <= 0:
	                layers = 2  
	        generate_json(term,session,layers = layers,words_per_layer = words_per_layer)
	        break
	    except ValueError:
	        print "Input argument is a stopword or does not exist on the corpus, please try again"


session = connectToCluster()
print "This script will create the json file and open it on your browser. For it to work, make sure you have your http server running on http://127.0.0.1:8080, if you want to see the visualization"
get_user_input()
open_html()
open_html("final_result_tree.html")
