def add():
    global flags, text, flag, tmp
    if flag != 0:
        flags.append(flag)
        text.append(tmp)
    tmp = ""


def chtenie():
    global flags, text, flag, tmp, ignore
    for s in fin:
        if s == fin[0]:
            continue
        for i in range(len(s)):
                if (ignore == True):
                    ignore = False
                    continue
                elif (s[i] == "\\"):
                    ignore = True
                elif (s[i] == "<"): # begin of tag (open or close)
                    if (s[i+1] == "/"): # close tag
                        add()
                        flag = 2
                        ignore = True
                    else: # open tag
                        flag, tmp = 1, ""
                elif (s[i] == ">"): # end of tag
                    add()
                    if (flag == 1): # if this tag == open tag then next field is statement
                        flag = 3
                    else: # if this tag == close tag after that tag will be some useless spaces
                        flag = 0
                else:
                    tmp += s[i] # add symbol to tag or statement


def massiv():
    global used, repeated
    for i in range(len(flags)-1):
        if (flags[i] == 2 and flags[i+1] == 1 and text[i] == text[i+1]):
            used.append(False)
            repeated.append(text[i])
            print(1)




def printTabs(tabs):
    res = ""
    for j in range(tabs): res += "  "
    return res


def writeInFile(output):
    fout = open("krik.txt", "w", encoding="utf-8")
    for s in output:
        if (s == output[0]): s = s.replace("\n", "")
        fout.write(s)
    fout.close()


def convert():
    #for i in range(len(flags)):
     #print(flags[i], text[i])
    massiv()
    tabs = 0
    res = ""
    output = []
    minusFlag = False
    for i in range(len(flags)):#!!!!!!!!!!!!!!!
        if (flags[i] == 1):
            if (res != ""):
                output.append(res)
                res = ""
                massiv()
            if (text[i] in repeated):
                if (used[repeated.index(text[i])] == 0):

                    used[repeated.index(text[i])] = 1
                    res += ("\n" + printTabs(tabs) + text[i] + ":")
                    tabs += 2
                    minusFlag = True

                else:
                    tabs += 2
                    res += ("\n" + printTabs(tabs-1) + " -" + text[i+1] + ":" )


            else:
                if (minusFlag == False):
                    res += ("\n" + printTabs(tabs) + text[i] + ":")
                else:
                    minusFlag = False
                    res += ("\n" + printTabs(tabs-1) + "  " + text[i] + ":")
                tabs += 1
        elif (flags[i] == 3):
            res += (" " + text[i])
        else:
            if (text[i] in repeated): tabs -= 1
            tabs -= 1
    writeInFile(output)

def openfile():
    global fin
    fin = open("xml.xml", "r", encoding="UTF-8").readlines()

flags =[]
flag = 0


#
text = []
tmp = ""
ignore = False
used = [] #
repeated = []


openfile()
chtenie()
convert()
