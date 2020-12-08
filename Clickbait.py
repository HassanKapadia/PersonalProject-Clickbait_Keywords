#Importing Required Librarires
import pandas as pd
import numpy as np
import os.path
from nltk import pos_tag
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from nltk.corpus import wordnet as wn
from nltk.stem import WordNetLemmatizer
from collections import defaultdict

#Converting list of strings to space separated strings
def convert(lst):
    return ' '.join(str(x) for x in lst)

#Preprocessing Data
raw_df=pd.read_csv("clickbait_data.csv")
clickbait_df=raw_df[raw_df['clickbait']==1][0:15000] #1-Clickbait and 0-NonClickbait
clickbait_df['headline']=[str(entry).lower() for entry in clickbait_df['headline']] #lower casing each entry
clickbait_df['headline']=[word_tokenize(str(entry),"english") for entry in clickbait_df['headline']] #tokenizing each entry

stop_words=set(stopwords.words('english'))
d = defaultdict(lambda: len(d))
tag_map = defaultdict(lambda: wn.NOUN)
tag_map['J'] = wn.ADJ
tag_map['V'] = wn.VERB
tag_map['R'] = wn.ADV

for index, entry in enumerate(clickbait_df['headline']):
    final_words = []
    word_Lemmatized = WordNetLemmatizer()
    for word, tag in pos_tag(entry):
        if word not in stop_words and word.isalpha():
            word_Final = word_Lemmatized.lemmatize(word, tag_map[tag[0]])
            final_words.append(d[word_Final]) #Using dictionary to convert strings to numbers
    clickbait_df.loc[index,'headline'] = convert(final_words) #Converting list of strings to space separated strings

key_list=list(d.keys()) #List of strings
val_list=list(d.values()) #List of numbers corresponding to strings
np_array=clickbait_df['headline'].to_numpy()
np.savetxt("apriori.txt",np_array,fmt="%s") #Storing Integer Representation of keywords in apriori.txt which is to be used by ClickbaitKeywords.java file.
#Preprocessing ends here

#Calling ClickbaitKeywords.java file that contains implementation of apriori algorithm
supp=input('\nNote:To get more frequent keywords enter higher support count value\nEnter support count:')
command="javac ClickbaitKeywords.java"
os.system(command)
command="java ClickbaitKeywords "+supp
os.system(command)
#Java file execution completed. Output is stored in aprioriOutput.txt file

#Preparing Output
with open('aprioriOutput.txt') as rd:
    freq_items=rd.read().split('\n')

freq_items.pop() #Eliminating blank line at the end.

item=[line.split(':')[0].strip('][') for line in freq_items]
count=[line.split(':')[1].strip() for line in freq_items]

count=[int(i) for i in count] #List of string to numbers

list1=np.array(item)
list2=np.array(count)

idx=np.argsort(list2) #Sorting in ascending order and retrieving index

#Using index to arrange the elements
list1=np.array(list1)[idx]
list2=np.array(list2)[idx]

#Reversing the array
list1=list1[::-1]
list2=list2[::-1]

print('\nEach list represents word/words that can be used to increase clickbait:')
for i in list1:
    final_list=[]
    printList=i.split(',')
    for j in range(0,len(printList)):
        k=int(printList[j]) #Actual Integer value
        final_list.append(key_list[val_list.index(k)]) #Retrieving Key value(Original String) from corresponding Integer value
    print(final_list)


