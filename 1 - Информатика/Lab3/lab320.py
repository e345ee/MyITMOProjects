import re


s = input()
p = re.findall('/', s)

if len(p) == 2:
    razdel = re.split('/', s)
    print(razdel)
    res = [len(re.findall(r'[аеёиоуыэюяАЁЕИОУЫЭЮЯ]', item)) for item in razdel]
    print(['Не Хайку', 'Хайку!'][res == [5, 7, 5]])
else:
    print("Это не хайку. Должно быть три строки")
