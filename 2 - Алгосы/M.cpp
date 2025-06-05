#include <bits/stdc++.h>
using namespace std;

const int MAXIMUM_VALUE = INT_MAX;
const int TOTAL_DIRECTIONS = 4;
const int DELTA_X[] = {-1, 0, 1, 0};
const int DELTA_Y[] = {0, 1, 0, -1};
const char DIRECTION_SYMBOLS[] = {'N', 'E', 'S', 'W'};

struct GridCell {
    int rowPosition;
    int columnPosition;
    int movementCost;

    bool operator>(const GridCell& comparisonCell) const {
        return movementCost > comparisonCell.movementCost;
    }
};

struct WorldMap {
    char** terrainLayout;
    int** pathCosts;
    char** directionHistory;
    int totalRows;
    int totalColumns;
};

WorldMap prepareWorldMap(int rows, int columns);
void processInputData(WorldMap& world);
void executePathfinding(WorldMap& world, int startRow, int startColumn, int endRow, int endColumn);
string buildPath(WorldMap& world, int originRow, int originColumn, int destinationRow, int destinationColumn);
void displayResults(int totalMovementCost, const string& pathSequence);
void releaseResources(WorldMap& world);

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);

    int dimensionRows, dimensionColumns;
    int beginningRow, beginningColumn;
    int objectiveRow, objectiveColumn;

    cin >> dimensionRows >> dimensionColumns;
    cin >> beginningRow >> beginningColumn;
    cin >> objectiveRow >> objectiveColumn;

    WorldMap gameWorld = prepareWorldMap(dimensionRows, dimensionColumns);
    processInputData(gameWorld);
    executePathfinding(gameWorld, beginningRow - 1, beginningColumn - 1, objectiveRow - 1, objectiveColumn - 1);

    if (gameWorld.pathCosts[objectiveRow - 1][objectiveColumn - 1] == MAXIMUM_VALUE) {
        cout << -1 << endl;
    } else {
        string finalPath = buildPath(
            gameWorld, beginningRow - 1, beginningColumn - 1,
            objectiveRow - 1, objectiveColumn - 1
        );
        displayResults(gameWorld.pathCosts[objectiveRow - 1][objectiveColumn - 1], finalPath);
    }

    releaseResources(gameWorld);
    return 0;
}

WorldMap prepareWorldMap(int rows, int columns) {
    WorldMap newWorld;
    newWorld.totalRows = rows;
    newWorld.totalColumns = columns;

    newWorld.terrainLayout = new char*[rows];
    newWorld.pathCosts = new int*[rows];
    newWorld.directionHistory = new char*[rows];

    for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
        newWorld.terrainLayout[rowIndex] = new char[columns];
        newWorld.pathCosts[rowIndex] = new int[columns];
        newWorld.directionHistory[rowIndex] = new char[columns];

        for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
            newWorld.pathCosts[rowIndex][columnIndex] = MAXIMUM_VALUE;
            newWorld.directionHistory[rowIndex][columnIndex] = ' ';
        }
    }

    return newWorld;
}

void processInputData(WorldMap& world) {
    char terrainType;
    for (int rowCounter = 0; rowCounter < world.totalRows; rowCounter++) {
        for (int columnCounter = 0; columnCounter < world.totalColumns; columnCounter++) {
            cin >> terrainType;
            world.terrainLayout[rowCounter][columnCounter] = terrainType;
        }
    }
}

void executePathfinding(WorldMap& world, int startRow, int startColumn, int goalRow, int goalColumn) {
    priority_queue<GridCell, vector<GridCell>, greater<GridCell>> explorationQueue;
    world.pathCosts[startRow][startColumn] = 0;
    explorationQueue.push(GridCell{startRow, startColumn, 0});

    while (!explorationQueue.empty()) {
        GridCell currentCell = explorationQueue.top();
        explorationQueue.pop();

        if (currentCell.rowPosition == goalRow && currentCell.columnPosition == goalColumn)
            break;

        if (currentCell.movementCost > world.pathCosts[currentCell.rowPosition][currentCell.columnPosition])
            continue;

        for (int directionIndex = 0; directionIndex < TOTAL_DIRECTIONS; directionIndex++) {
            int neighborRow = currentCell.rowPosition + DELTA_X[directionIndex];
            int neighborColumn = currentCell.columnPosition + DELTA_Y[directionIndex];

            if (neighborRow < 0 || neighborRow >= world.totalRows ||
                neighborColumn < 0 || neighborColumn >= world.totalColumns ||
                world.terrainLayout[neighborRow][neighborColumn] == '#')
                continue;

            int updatedCost = currentCell.movementCost;
            if (world.terrainLayout[neighborRow][neighborColumn] == '.')
                updatedCost += 1;
            else if (world.terrainLayout[neighborRow][neighborColumn] == 'W')
                updatedCost += 2;

            if (updatedCost < world.pathCosts[neighborRow][neighborColumn]) {
                world.pathCosts[neighborRow][neighborColumn] = updatedCost;
                world.directionHistory[neighborRow][neighborColumn] = DIRECTION_SYMBOLS[directionIndex];
                explorationQueue.push(GridCell{neighborRow, neighborColumn, updatedCost});
            }
        }
    }
}

string buildPath(WorldMap& world, int originRow, int originColumn, int destinationRow, int destinationColumn) {
    string completePath;
    int currentRow = destinationRow;
    int currentColumn = destinationColumn;

    while (currentRow != originRow || currentColumn != originColumn) {
        char moveDirection = world.directionHistory[currentRow][currentColumn];
        completePath += moveDirection;

        for (int dirIndex = 0; dirIndex < TOTAL_DIRECTIONS; dirIndex++) {
            if (DIRECTION_SYMBOLS[dirIndex] == moveDirection) {
                currentRow -= DELTA_X[dirIndex];
                currentColumn -= DELTA_Y[dirIndex];
                break;
            }
        }
    }

    reverse(completePath.begin(), completePath.end());
    return completePath;
}

void displayResults(int totalMovementCost, const string& pathSequence) {
    cout << totalMovementCost << '\n' << pathSequence << endl;
}

void releaseResources(WorldMap& world) {
    for (int row = 0; row < world.totalRows; row++) {
        delete[] world.terrainLayout[row];
        delete[] world.pathCosts[row];
        delete[] world.directionHistory[row];
    }
    delete[] world.terrainLayout;
    delete[] world.pathCosts;
    delete[] world.directionHistory;
}
