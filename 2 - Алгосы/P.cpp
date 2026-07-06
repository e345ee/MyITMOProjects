#include <bits/stdc++.h>
using namespace std;

const int MAX_CITIES = 1000;

int FuelCosts[MAX_CITIES][MAX_CITIES];
int CityParent[MAX_CITIES];
int CityRank[MAX_CITIES];
bool VisitedFlags[MAX_CITIES];
int DfsOrder[MAX_CITIES];
int CityCount;

void InitializeCityStructures() {
    for (int city = 0; city < CityCount; ++city) {
        CityParent[city] = city;
        CityRank[city] = 0;
    }
}

int FindCityRoot(int city) {
    if (city == CityParent[city])
        return city;
    return CityParent[city] = FindCityRoot(CityParent[city]);
}

void MergeCitySets(int cityA, int cityB) {
    cityA = FindCityRoot(cityA);
    cityB = FindCityRoot(cityB);
    if (cityA != cityB) {
        if (CityRank[cityA] < CityRank[cityB])
            swap(cityA, cityB);
        CityParent[cityB] = cityA;
        if (CityRank[cityA] == CityRank[cityB])
            CityRank[cityA]++;
    }
}

bool CheckWeakConnectivity(int currentFuel) {
    InitializeCityStructures();

    for (int from = 0; from < CityCount; ++from) {
        for (int to = 0; to < CityCount; ++to) {
            if (FuelCosts[from][to] <= currentFuel || FuelCosts[to][from] <= currentFuel) {
                MergeCitySets(from, to);
            }
        }
    }

    int mainRoot = FindCityRoot(0);
    for (int city = 1; city < CityCount; ++city) {
        if (FindCityRoot(city) != mainRoot)
            return false;
    }
    return true;
}

void PerformFirstDfsPass(int currentCity, int currentFuel, int& orderIndex) {
    VisitedFlags[currentCity] = true;
    for (int nextCity = 0; nextCity < CityCount; ++nextCity) {
        if (!VisitedFlags[nextCity] && FuelCosts[currentCity][nextCity] <= currentFuel) {
            PerformFirstDfsPass(nextCity, currentFuel, orderIndex);
        }
    }
    DfsOrder[orderIndex++] = currentCity;
}

void PerformSecondDfsPass(int currentCity, int currentFuel) {
    VisitedFlags[currentCity] = true;
    for (int prevCity = 0; prevCity < CityCount; ++prevCity) {
        if (!VisitedFlags[prevCity] && FuelCosts[prevCity][currentCity] <= currentFuel) {
            PerformSecondDfsPass(prevCity, currentFuel);
        }
    }
}

bool CheckStrongConnectivity(int currentFuel) {
    if (!CheckWeakConnectivity(currentFuel))
        return false;

    fill(VisitedFlags, VisitedFlags + CityCount, false);
    int orderIndex = 0;

    for (int city = 0; city < CityCount; ++city) {
        if (!VisitedFlags[city]) {
            PerformFirstDfsPass(city, currentFuel, orderIndex);
        }
    }

    reverse(DfsOrder, DfsOrder + CityCount);
    fill(VisitedFlags, VisitedFlags + CityCount, false);
    int componentCount = 0;

    for (int i = 0; i < CityCount; ++i) {
        int city = DfsOrder[i];
        if (!VisitedFlags[city]) {
            PerformSecondDfsPass(city, currentFuel);
            componentCount++;
            if (componentCount > 1)
                return false;
        }
    }

    return true;
}

int FindMinimalFuelCapacity() {
    int maxEdgeFuel = 0;
    for (int from = 0; from < CityCount; ++from) {
        for (int to = 0; to < CityCount; ++to) {
            if (from != to) {
                maxEdgeFuel = max(maxEdgeFuel, FuelCosts[from][to]);
            }
        }
    }

    int lowFuel = 0;
    int highFuel = maxEdgeFuel;
    int optimalFuel = maxEdgeFuel;

    while (lowFuel <= highFuel) {
        int midFuel = lowFuel + (highFuel - lowFuel) / 2;
        if (CheckStrongConnectivity(midFuel)) {
            optimalFuel = midFuel;
            highFuel = midFuel - 1;
        } else {
            lowFuel = midFuel + 1;
        }
    }

    return optimalFuel;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> CityCount;
    for (int from = 0; from < CityCount; ++from) {
        for (int to = 0; to < CityCount; ++to) {
            cin >> FuelCosts[from][to];
        }
    }

    cout << FindMinimalFuelCapacity() << '\n';
    return 0;
}
