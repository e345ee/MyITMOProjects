#include <climits>
#include <cstdlib>
#include <deque>
#include <iostream>
#include <list>
#include <queue>
#include <stack>

using namespace std;

struct StTrk {
    stack<pair<int, int>> d;

    void ad(int v) {
        if (d.empty()) {
            d.push({v, v});
        } else {
            int new_min = (v < d.top().second) ? v : d.top().second;
            d.push({v, new_min});
        }
    }

    void rm() {
        if (d.empty()) {
            cerr << "ОШИБКА: Попытка удалить из пустого стека!\n";
            exit(EXIT_FAILURE);
        }
        d.pop();
    }

    int mnVal() const {
        if (d.empty()) {
            cerr << "ОШИБКА: Запрос минимума пустого стека!\n";
            return INT_MAX;
        }
        return d.top().second;
    }

    bool emty() const {
        return d.empty();
    }
};

void peremestit(StTrk& q, StTrk& w) {
    while (!w.emty()) {
        if (w.d.empty()) {
            cerr << "ОШИБКА: Перемещение из пустого стека!\n";
            exit(EXIT_FAILURE);
        }
        int tmp = w.d.top().first;
        w.rm();
        q.ad(tmp);
    }
}

int cM(
    StTrk& i,
    StTrk& o,
    priority_queue<pair<int, int>, deque<pair<int, int>>, greater<>>& pQ,
    int kkk,
    size_t id
) {
    if (kkk <= 0) {
        cerr << "ОШИБКА: Неверный размер окна (" << kkk << ")!\n";
        exit(EXIT_FAILURE);
    }

    while (!pQ.empty() && (pQ.top().second + static_cast<size_t>(kkk)) <= id) {
        pQ.pop();
    }

    if (o.emty())
        peremestit(o, i);

    int m_1 = min(i.mnVal(), o.mnVal());
    int m_2 = pQ.empty() ? INT_MAX : pQ.top().first;

    return min(m_1, m_2);
}

list<int> gSlidMin(const deque<int>& a, int k) {
    if (a.empty()) {
        cerr << "ОШИБКА: Пустой входной массив!\n";
        exit(EXIT_FAILURE);
    }

    StTrk sIn, sOut;
    priority_queue<pair<int, int>, deque<pair<int, int>>, greater<>> pqW;
    list<int> rez;

    for (size_t j = 0; j < a.size(); ++j) {
        sIn.ad(a[j]);
        pqW.push({a[j], static_cast<int>(j)});

        if (j >= static_cast<size_t>(k) - 1) {
            if (k <= 0) {
                cerr << "ОШИБКА: Размер окна должен быть > 0!\n";
                exit(EXIT_FAILURE);
            }

            int cm = cM(sIn, sOut, pqW, k, j);
            rez.push_back(cm);

            if (sOut.emty()) {
                cerr << "ОШИБКА: Выходной стек пуст при удалении!\n";
                exit(EXIT_FAILURE);
            }
            sOut.rm();
        }
    }
    return rez;
}

void ldData(int& sz, int& w, deque<int>& dt) {
    if (!(cin >> sz >> w)) {
        cerr << "ОШИБКА: Неверный формат ввода размеров!\n";
        exit(EXIT_FAILURE);
    }

    if (sz <= 0 || w <= 0) {
        cerr << "ОШИБКА: Размеры должны быть положительными!\n";
        exit(EXIT_FAILURE);
    }

    dt.resize(sz);
    for (int t = 0; t < sz; ++t) {
        if (!(cin >> dt[t])) {
            cerr << "ОШИБКА: Неверный формат элемента " << t << "!\n";
            exit(EXIT_FAILURE);
        }
    }
}

int main() {
    int N, K;
    deque<int> D;

    try {
        ldData(N, K, D);

        if (K > N) {
            cerr << "ОШИБКА: Размер окна (" << K << ") больше размера массива (" << N << ")!\n";
            return EXIT_FAILURE;
        }

        list<int> mns = gSlidMin(D, K);

        for (int x : mns) {
            cout << x << " ";
        }
        cout << endl;

    } catch (...) {
        cerr << "Непредвиденная ошибка!\n";
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}
