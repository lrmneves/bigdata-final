'''Same code as in preprocess.py'''
import os,fnmatch
from nltk import word_tokenize
from nltk.stem.porter import PorterStemmer
from nltk.corpus import stopwords
import re
from joblib import Parallel, delayed

def process_term(term):
    '''Process an input term. Throws ValueError exception if term could not be processed.'''
    processed_list = process_tokens([term])
    if len(processed_list) == 0:
        raise ValueError('Input argument is a stopword or does not exist on the corpus')
    return processed_list[0]

def process_tokens(tokens):
    '''Treats a list of tokens, removing stopwords, special chars and stemming the terms. 
    For single term, use process_term'''
    
    research_stopwords = set(["et","al","fig"])
    
    no_special_and_stopwords = [re.sub('[^A-Za-z0-9]+', '', word) for word in tokens if (word not in stopwords.words('english') 
                                                                                         and word not in research_stopwords)]
    #stem the terms
    porter_stemmer = PorterStemmer()
    return[porter_stemmer.stem(word) for word in no_special_and_stopwords if word != ""]

def process_file(filepath,f):
    '''For each file, creates a new file with tonekized and stemmed words.'''
    #open original file and create new file
    current = open(filepath + os.sep + f,"r")
    proc_file = open(os.getcwd() + os.sep +"processedDocuments"+os.sep + "process_"+f,"w")
    #tokenize the words and remove tabs and eol
    tokens = [word.strip("\n").strip("\t").strip() for word in word_tokenize(current.read().decode("utf8")) if word.strip("\n").strip("\t").strip()!= ""]
    #preprocess tokens
    stemmed_words = process_tokens(tokens)
    #persist changes to disk
    proc_file.write(" ".join(stemmed_words))
    proc_file.close()
    current.close()
    
def preprocess_current_folder():
    #if output folder does not exist, creates it
    if not os.path.exists(os.getcwd() + os.sep +"processedDocuments"):
        os.makedirs(os.getcwd() + os.sep +"processedDocuments")
    #Walk through all folders and subfolders looking for txt files
    for folder in os.listdir(os.getcwd()):
        #skip the folder with processed documents
        if folder == "processedDocuments":
            continue
        if os.path.isdir(folder) and not folder.startswith("."):

            for subdir, dirs, files in os.walk(os.getcwd() + os.sep + folder):

                for dir in dirs:
                    filepath = subdir + os.sep + dir
                    #parallel for to handle multiple files at once
                    Parallel(n_jobs=-1)(delayed(process_file)(filepath,f) for f in os.listdir(filepath) if f.endswith(".txt"))
                    
preprocess_current_folder()