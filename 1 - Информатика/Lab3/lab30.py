print(368748%5)
#302 8-{O Садовой Григорий Владимирович
import re

smail = re.escape(input())
s = input()
print(smail)
print(s)
print(len(re.findall(smail, s)))