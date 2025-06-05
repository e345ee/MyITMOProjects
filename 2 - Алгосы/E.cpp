#include <bits/stdc++.h>
using namespace std;

int check(const int box[], int n, int cows, int distance) {
    int count = 1;
    int last = box[0];
    for (int i = 1; i < n; ++i) {
        if (box[i] - last >= distance) {
            ++count;
            last = box[i];
        }
    }
    return (count >= cows);
}

inline void init(int& left, int& right, int& minMax, const int arr[], int n) {
    left = 0;
    right = arr[n - 1] - arr[0];
    minMax = 0;
}

int main() {
    int n, k;
    cin >> n >> k;

    int left, right, minMax;
    int* box = new int[n];

    for (int i = 0; i < n; ++i) {
        cin >> box[i];
    }

    init(left, right, minMax, box, n);

    while (right >= left) {
        int mid = left + (right - left) / 2;
        if (check(box, n, k, mid)) {
            minMax = mid;
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }

    cout << minMax << endl;
    delete[] box;
    return 0;
}
