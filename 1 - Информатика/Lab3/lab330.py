import re

#Садовой Григорий 
mas = []
s = input()
mix = []
while s != "стоп":
    mas.append(s)
    s = input()
print()
dlin = len(mas)
print(dlin)

for i in  range (-1, len(mas) - 1):
  mas[i] = re.sub(r"(\b([А-Я])\w*(?:[- ]$\2\w*)?\s+(?:\2.){2} Р3107)"," ", mas[i])
itog = list(filter(None, mas))

for b in range(len(itog)):
    print (itog[b])