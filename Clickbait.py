from sys import stdin

import pandas as pd
import numpy as np
import subprocess
import os.path
from nltk import pos_tag
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from nltk.corpus import wordnet as wn
from nltk.stem import WordNetLemmatizer
from collections import defaultdict

def convert(lst):
    return ' '.join(str(x) for x in lst)

raw_df=pd.read_csv("clickbait_data.csv")
clickbait_df=raw_df[raw_df['clickbait']==1][0:15000]
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
            final_words.append(d[word_Final])
    clickbait_df.loc[index,'headline'] = convert(final_words)

key_list=list(d.keys())
val_list=list(d.values())
np_array=clickbait_df['headline'].to_numpy()
np.savetxt("apriori.txt",np_array,fmt="%s")

supp=input('\nNote:To get more frequent keywords enter higher support count value\nEnter support count:')
command="javac ClickbaitKeywords.java"
os.system(command)
command="java ClickbaitKeywords "+supp
os.system(command)

with open('aprioriOutput.txt') as rd:
    freq_items=rd.read().split('\n')

freq_items.pop()

item=[line.split(':')[0].strip('][') for line in freq_items]
count=[line.split(':')[1].strip() for line in freq_items]

#for i in range(0,len(item)):
    #print(item[i],'\t\t',count[i],'\n')

#item=[int(i) for i in item]
count=[int(i) for i in count]

list1=np.array(item)
list2=np.array(count)

idx=np.argsort(list2)

list1=np.array(list1)[idx]
list2=np.array(list2)[idx]

list1=list1[::-1]
list2=list2[::-1]

#for i in range(0,len(list1)):
   # print(list1[i],':\t',list2[i])

print('\nEach list represents word/words that can be used to increase clickbait:')
for i in list1:
    final_list=[]
    printList=i.split(',')
    for j in range(0,len(printList)):
        k=int(printList[j])
        final_list.append(key_list[val_list.index(k)])
    print(final_list)


