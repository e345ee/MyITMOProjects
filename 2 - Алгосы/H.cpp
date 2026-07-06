#include <bits/stdc++.h>
using namespace std;

void sorting(int* array, int n) {
    vector<int> temp(array, array + n);
    make_heap(temp.begin(), temp.end());
    sort_heap(temp.begin(), temp.end());
    reverse(temp.begin(), temp.end());
    copy(temp.begin(), temp.end(), array);
}

int calculateTotal(int* arr, int n, int k) {
    int total = 0;
    for (int i = 0; i < n; ++i) {
        if ((i + 1) % k != 0) {
            total += arr[i];
        }
    }
    return total;
}

int main() {
    int n, k;
    cin >> n >> k;

    int* box = new int[n];
    for (int i = 0; i < n; ++i) {
        cin >> box[i];
    }

    sorting(box, n);
    int sum = calculateTotal(box, n, k);
    cout << sum << endl;

    delete[] box;
    return 0;
}
