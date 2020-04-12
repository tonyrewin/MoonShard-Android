#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import re
import html
import requests

xml_string_rx = re.compile(r'(?<=:(hint|text)=\")((?!\@strings\/)[^\"]+)(?=\")', re.UNICODE)
xml_comment_rx = re.compile(r'\<\!\-\-((?!\-\-\>)[\s\S])*\-\-\>', re.IGNORECASE)

src_string_rx = re.compile(r'(?<=(\"))([^\"]*?[а-яёЁА-Я]+[^\"]*?)(?=\")', re.UNICODE) # need a second group
src_comment_rx = re.compile(r'(/\*((?!\*/).)*\*/|//[^\n]*)', re.S | re.I)

rus_rx = re.compile(r'[а-яёЁА-Я}]', re.UNICODE)
var_rx = re.compile(r'[\$\{\}]', re.IGNORECASE | re.UNICODE)
id_rx = re.compile(r'[^a-z0-9_]', re.IGNORECASE)
snames = []

curdir = os.path.dirname(os.path.realpath(__file__))

def _translate(to_translate, src='ru', dst='en'):
	# based on code from https://github.com/rishabhdugar/android-localization-helper

	# send request
	r = requests.get("https://translate.google.com/m?hl="+dst+"&sl="+src+"&q=" + to_translate.replace(" ", "+"))
	# set markers that enclose the charset identifier
	beforecharset = 'charset='
	aftercharset = '" http-equiv'
	# extract charset 
	parsed1 = r.text[r.text.find(beforecharset)+len(beforecharset):]
	parsed2 = parsed1[:parsed1.find(aftercharset)]
	# convert html tags  
	text = html.unescape(r.text) 
	# set markers that enclose the wanted translation
	before_trans = 'class="t0">'
	after_trans = '</div><form'
	# extract translation and return it
	parsed1 = r.text[r.text.find(before_trans)+len(before_trans):]
	parsed2 = parsed1[:parsed1.find(after_trans)]
	return html.unescape(parsed2)

def print_translated(directory, string_rx, replacement, comment_rx):
	path = curdir + directory
	# walk activities
	for (dirpath, dirnames, filenames) in os.walk(path):
		filenames.sort()
		for filename in filenames:
			filepath = os.path.join(dirpath, filename)
			with open(filepath, 'r') as f:
				content = f.read()
				f.close()
				# remove all the comments from content
				content_updated = comment_rx.sub("", content)
				# find all matches
				found_strings = string_rx.findall(content_updated)
				# print(found_strings)
				if len(found_strings) > 0:
					fpath = filepath.replace(path, '')
					print("\t<!-- " + fpath + " -->")
					for s in found_strings:
						replace = True
						# take group
						svalue = s[1]
						# check if it is with vars
						if var_rx.search(svalue) is not None:
							print("\t\t<!-- " + svalue.replace('\n','')[:50] + " -->")
							replace = False
						else:
							# translate string
							if rus_rx.search(svalue) is not None:
								svalue = _translate(svalue)
						# create the id from translated
						sname = '_'.join(svalue.lower().strip().replace(" ", "_").split('_')[:5])
						# remove invalid for id chars
						sname = id_rx.sub('', sname)
						if len(sname)>0:
							# adding to strings.xml if not there
							if sname not in snames:
								# id should start with alpha char
								if not sname[0].isalpha():
									# create an ID from filename adding _text<number>
									sname = filename.split('.')[0].replace('fragment_', '').replace('activity_','').replace('bottom_sheet_', '') + '_text' + str(len(snames))
								print("\t\t<string name=\"" + sname + "\">" + svalue + "</string>")
								snames.append(sname)
							# replacement
							if replace:
								content_updated = content_updated.replace(s[1], replacement % sname)
					#origfile = open(filepath + '.orig', 'w')
					#origfile.write(content)
					#origfile.close()
					newfile = open(filepath, 'w')
					newfile.write(content_updated)
					newfile.close()
					print("")

if __name__ == "__main__":
	#print("<resources>\n")
	#print_translated(
	#	"/app/src/main/java/io/moonshard/moonshard/", 
	#	"/test",
	#	src_string_rx, 
	#	"\" + getString(R.string.%s) + \"",
	#	src_comment_rx
	#	)
	#print_translated(
	#	"/app/src/main/res/layout", 
	#	"/test",
	#	xml_string_rx, 
	#	"@strings/%s", 
	#	xml_comment_rx)
	for s in open(curdir + '/app/src/main/res/values/strings.xml').readlines():
		sparts = s.split('>', 1)
		if len(sparts)>1:
			svalue = sparts[1].replace('</string>', '')
			translated = _translate(svalue, 'en', 'ru')
			if len(translated)>1 and '>' not in translated:
				s = sparts[0] +'>' + translated + '</string>'
		print(s)
	#print("</resources>")