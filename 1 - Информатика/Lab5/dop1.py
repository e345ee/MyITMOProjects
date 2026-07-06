import yaml
import xmltodict as xmltodict


newfile = xmltodict.parse(open("xml.xml", "r", encoding="utf8").read()) #преобразуем в словарь
yaml.dump(newfile, open("bebra.yaml", "w"), allow_unicode=True)