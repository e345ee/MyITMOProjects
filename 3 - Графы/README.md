# Граф знаний по Genshin Impact

![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![RDF](https://img.shields.io/badge/RDF-0C5DA5?style=for-the-badge&logo=w3c&logoColor=white)
![OWL](https://img.shields.io/badge/OWL-5A4FCF?style=for-the-badge&logo=w3c&logoColor=white)
![SPARQL](https://img.shields.io/badge/SPARQL-1F7A5C?style=for-the-badge&logo=w3c&logoColor=white)
![RDFLib](https://img.shields.io/badge/RDFLib-2B6CB0?style=for-the-badge&logo=python&logoColor=white)
![BeautifulSoup](https://img.shields.io/badge/BeautifulSoup-59666C?style=for-the-badge&logo=python&logoColor=white)
![Jupyter](https://img.shields.io/badge/Jupyter-F37626?style=for-the-badge&logo=jupyter&logoColor=white)
![PyTorch](https://img.shields.io/badge/PyTorch-EE4C2C?style=for-the-badge&logo=pytorch&logoColor=white)
![scikit-learn](https://img.shields.io/badge/scikit--learn-F7931E?style=for-the-badge&logo=scikitlearn&logoColor=white)

Проект по дисциплине «Графы знаний»: командный граф знаний для Genshin
Impact, который помогает подбирать отряды, оружие и артефакты с учетом
стихий, уязвимостей противников, редкости персонажей и рекомендаций по
снаряжению.

Команда: Артем Назин, Данила Мартышов, Григорий Садовой.

## Что внутри

- `ontology.rdf` - готовая RDF/OWL-онтология по Genshin Impact.
- `ontology_empty.rdf` - минимальная версия онтологии со схемой классов и свойств.
- `sparql/` - набор SPARQL-запросов к графу знаний.
- `embeddings.ipynb` - эксперименты с graph embeddings: DistMult, PyKEEN, кластеризация и поиск похожих сущностей.
- `GrafsOneMore-master/` - парсеры данных, обработчики CSV и генератор RDF.
- `Genshin Impact.pdf` - презентация проекта.

## Модель графа

В графе описаны персонажи, команды, противники, оружие, типы оружия,
артефакты, стихии, регионы, редкость и характеристики артефактов.

Основные связи:

- персонаж -> стихия, регион, редкость, тип оружия;
- персонаж -> лучшее и дополнительное оружие;
- персонаж -> лучшие и дополнительные наборы артефактов;
- персонаж -> рекомендуемые основные и дополнительные характеристики артефактов;
- команда -> участники команды;
- противник -> стихии, к которым он уязвим.

Готовая онтология содержит около **7.7k RDF-триплов**, **13 классов**,
**14 объектных свойств** и почти **900 описанных сущностей**.

## SPARQL-запросы

В `sparql/queries/` лежат запросы, которые отвечают на прикладные игровые
вопросы:

- какие команды подходят против босса `The Knave`;
- какие команды с Anemo-персонажами подходят против Anemo-уязвимых боссов;
- какой сет артефактов подходит наибольшему числу персонажей против `Azhdaha`;
- какие Cryo-персонажи могут использовать `Staff of Homa`;
- какие команды формата 2 x 4★ + 2 x 5★ подходят против `Emperor of Fire and Iron`.

## Пайплайн

1. Парсеры собирают данные о персонажах, командах и противниках из открытых
   источников по Genshin Impact.
2. CSV-данные нормализуются и преобразуются в сущности и связи.
3. Генератор RDF собирает итоговую онтологию.
4. SPARQL-запросы используются для проверки гипотез и получения рекомендаций.
5. В ноутбуке обучаются embeddings графа для поиска похожих сущностей,
   визуализации и кластеризации.
