# Genshin Impact Knowledge Graph

![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![RDF](https://img.shields.io/badge/RDF-0C5DA5?style=for-the-badge&logo=w3c&logoColor=white)
![OWL](https://img.shields.io/badge/OWL-5A4FCF?style=for-the-badge&logo=w3c&logoColor=white)
![SPARQL](https://img.shields.io/badge/SPARQL-1F7A5C?style=for-the-badge&logo=w3c&logoColor=white)
![RDFLib](https://img.shields.io/badge/RDFLib-2B6CB0?style=for-the-badge&logo=python&logoColor=white)
![BeautifulSoup](https://img.shields.io/badge/BeautifulSoup-59666C?style=for-the-badge&logo=python&logoColor=white)
![Jupyter](https://img.shields.io/badge/Jupyter-F37626?style=for-the-badge&logo=jupyter&logoColor=white)
![PyTorch](https://img.shields.io/badge/PyTorch-EE4C2C?style=for-the-badge&logo=pytorch&logoColor=white)
![scikit-learn](https://img.shields.io/badge/scikit--learn-F7931E?style=for-the-badge&logo=scikitlearn&logoColor=white)

Project for the Knowledge Graphs course: a team-built knowledge graph for
Genshin Impact that helps choose teams, weapons, and artifacts while accounting
for elements, enemy vulnerabilities, character rarity, and equipment
recommendations.

Team: Artem Nazin, Danila Martyshov, Grigory Sadovoy.

## What's Inside

- `ontology.rdf` - complete RDF/OWL ontology for Genshin Impact.
- `ontology_empty.rdf` - minimal ontology version with class and property schema.
- `sparql/` - a set of SPARQL queries for the knowledge graph.
- `embeddings.ipynb` - graph embedding experiments: DistMult, PyKEEN, clustering, and similar entity search.
- `GrafsOneMore-master/` - data parsers, CSV processors, and RDF generator.
- `Genshin Impact.pdf` - project presentation.

## Graph Model

The graph describes characters, teams, enemies, weapons, weapon types,
artifacts, elements, regions, rarity, and artifact stats.

Main relations:

- character -> element, region, rarity, weapon type;
- character -> best and secondary weapons;
- character -> best and secondary artifact sets;
- character -> recommended main and secondary artifact stats;
- team -> team members;
- enemy -> elements it is vulnerable to.

The completed ontology contains about **7.7k RDF triples**, **13 classes**,
**14 object properties**, and almost **900 described entities**.

## SPARQL Queries

The `sparql/queries/` directory contains queries that answer practical gameplay
questions:

- which teams are suitable against the `The Knave` boss;
- which teams with Anemo characters are suitable against Anemo-vulnerable bosses;
- which artifact set fits the largest number of characters against `Azhdaha`;
- which Cryo characters can use `Staff of Homa`;
- which 2 x 4★ + 2 x 5★ teams are suitable against `Emperor of Fire and Iron`.

## Pipeline

1. Parsers collect data about characters, teams, and enemies from open Genshin
   Impact sources.
2. CSV data is normalized and converted into entities and relations.
3. The RDF generator builds the final ontology.
4. SPARQL queries are used to test hypotheses and generate recommendations.
5. The notebook trains graph embeddings for similar entity search,
   visualization, and clustering.
