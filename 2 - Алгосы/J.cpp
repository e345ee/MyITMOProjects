#include <climits>
#include <cstdlib>
#include <deque>
#include <iostream>
#include <memory>
#include <queue>
#include <set>

using namespace std;

template <typename T>
class ToyShelf {
private:
    size_t cap;
    unique_ptr<T[]> toys;

public:
    ToyShelf(size_t cap) : cap(cap), toys(make_unique<T[]>(cap)) {
    }

    T& operator[](size_t id) {
        return toys[id];
    }

    size_t getCap() const {
        return cap;
    }
};

struct ToyComp {
    bool operator()(const pair<long, long>& a, const pair<long, long>& b) const {
        long aNext = (a.first == -1) ? LONG_MAX : a.first;
        long bNext = (b.first == -1) ? LONG_MAX : b.first;
        return aNext > bNext;
    }
};

void readToys(long& nToys, long& floorCap, long& nPlays, deque<long>& playSeq) {
    cin >> nToys >> floorCap >> nPlays;
    if (nToys <= 0 || floorCap <= 0 || nPlays <= 0) {
        cerr << "Error 1\n";
        exit(1);
    }

    playSeq.resize(nPlays);
    for (auto& t : playSeq) {
        cin >> t;
        t--;
    }
}

void setupQueues(const deque<long>& playSeq, ToyShelf<queue<long>>& shelf) {
    size_t step = 0;
    for (const auto& toy : playSeq) {
        shelf[toy].push(step++);
    }
}

void simulatePlay(
    const deque<long>& playSeq,
    ToyShelf<queue<long>>& shelf,
    long floorCap,
    long& opCount
) {
    set<long> floor;
    multiset<pair<long, long>, ToyComp> toyPrio;

    for (size_t currTime = 0; currTime < playSeq.size(); ++currTime) {
        long currToy = playSeq[currTime];
        auto& q = shelf[currToy];
        const long currTimeLong = static_cast<long>(currTime); // Приведение типа

        if (!q.empty() && q.front() == currTimeLong) {
            q.pop();
        }

        if (floor.find(currToy) == floor.end()) {
            if (floor.size() >= static_cast<size_t>(floorCap)) {
                auto it = toyPrio.begin();
                floor.erase(it->second);
                toyPrio.erase(it);
            }
            ++opCount;
            floor.insert(currToy);
        }

        long nextPlay = q.empty() ? -1 : q.front();
        toyPrio.emplace(nextPlay, currToy);
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long nToys, floorCap, nPlays, opCount = 0;
    deque<long> playSeq;

    readToys(nToys, floorCap, nPlays, playSeq);

    ToyShelf<queue<long>> shelf(nToys);
    setupQueues(playSeq, shelf);
    simulatePlay(playSeq, shelf, floorCap, opCount);

    cout << opCount << "\n";
    return 0;
}
