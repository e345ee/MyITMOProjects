def add():    # добавление информации из временных хранилищ в нормальные
    global flags, text, flag, tmp
    if flag != 0:
        flags.append(flag)
        text.append(tmp)
    tmp = ""


def open_file():
    global file
    file = open("xml.xml", "r", encoding="UTF-8").readlines()


def chtenie():
    global flags, text, flag, tmp, slash
    for s in file:
        if s == file[0]:
            continue
        for i in range(len(s)):
                if (slash == True): #экранирование слеша
                    slash = False
                    continue
                elif (s[i] == "\\"):
                    slash = True
                elif (s[i] == "<"):
                    if (s[i+1] == "/"):
                        add()
                        flag = 2
                        ignore = True
                    else:
                        flag, tmp = 1, ""
                elif (s[i] == ">"):
                    add()
                    if (flag == 1):
                        flag = 3
                    else:
                        flag = 0
                else:
                    tmp += s[i]


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
    fout = open(".txt", "w", encoding="utf-8")
    for s in output:
        if (s == output[0]): s = s.replace("\n", "") #надо убрать пустоту в начале файла
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
                    res += ("\n" + printTabs(tabs+1) + "-" + text[i] + ":" + " ")
                    tabs += 2
                    minusFlag = True

                else:
                    tabs += 2
                    res += ("\n" + printTabs(tabs-1) + "-" + text[i] + ":" + " ")

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

####################


flags =[]
flag = 0



text = [] #Массив полученного текста
tmp = "" #временный массив для сортировки
slash = False #экранирование слеша
used = [] # show was used or wasn’t used repeated tag
repeated = []

open_file()
chtenie()
convert()
