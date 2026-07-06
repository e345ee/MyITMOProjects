#include <bits/stdc++.h>
#define MAXIMUM_NUMBER_OF_STUDENTS_IN_THE_CLASS 100

typedef struct DisjointSetUnionWithGroups {
    int leader_of_each_student[MAXIMUM_NUMBER_OF_STUDENTS_IN_THE_CLASS];
    int rank_level_of_each_set[MAXIMUM_NUMBER_OF_STUDENTS_IN_THE_CLASS];
    int group_assignment_for_each_student[MAXIMUM_NUMBER_OF_STUDENTS_IN_THE_CLASS];
} DisjointSetUnionWithGroups;

void initializeDisjointSetUnion(DisjointSetUnionWithGroups* dsu, int total_students) {
    for (int i = 0; i < total_students; i++) {
        dsu->leader_of_each_student[i] = i;
        dsu->rank_level_of_each_set[i] = 0;
        dsu->group_assignment_for_each_student[i] = 0;
    }
}

typedef struct FindRootAndGroupResult {
    int root_of_the_set;
    int group_of_the_student_relative_to_root;
} FindRootAndGroupResult;

FindRootAndGroupResult findRootWithPathCompressionAndGroupCalculation(
    DisjointSetUnionWithGroups* dsu, int student
) {
    if (dsu->leader_of_each_student[student] != student) {
        FindRootAndGroupResult parent_result =
            findRootWithPathCompressionAndGroupCalculation(dsu, dsu->leader_of_each_student[student]);

        dsu->group_assignment_for_each_student[student] ^= parent_result.group_of_the_student_relative_to_root;
        dsu->leader_of_each_student[student] = parent_result.root_of_the_set;
    }

    FindRootAndGroupResult result;
    result.root_of_the_set = dsu->leader_of_each_student[student];
    result.group_of_the_student_relative_to_root = dsu->group_assignment_for_each_student[student];
    return result;
}

int uniteTwoSetsWithBipartitionCheck(
    DisjointSetUnionWithGroups* dsu, int a, int b
) {
    FindRootAndGroupResult root_a = findRootWithPathCompressionAndGroupCalculation(dsu, a);
    FindRootAndGroupResult root_b = findRootWithPathCompressionAndGroupCalculation(dsu, b);

    if (root_a.root_of_the_set == root_b.root_of_the_set) {
        return root_a.group_of_the_student_relative_to_root != root_b.group_of_the_student_relative_to_root;
    }

    if (dsu->rank_level_of_each_set[root_a.root_of_the_set] > dsu->rank_level_of_each_set[root_b.root_of_the_set]) {
        std::swap(root_a, root_b);
    }

    dsu->leader_of_each_student[root_a.root_of_the_set] = root_b.root_of_the_set;
    dsu->group_assignment_for_each_student[root_a.root_of_the_set] =
        root_a.group_of_the_student_relative_to_root ^
        root_b.group_of_the_student_relative_to_root ^ 1;

    if (dsu->rank_level_of_each_set[root_a.root_of_the_set] ==
        dsu->rank_level_of_each_set[root_b.root_of_the_set]) {
        dsu->rank_level_of_each_set[root_b.root_of_the_set]++;
    }

    return 1;
}

int canDivideStudentsIntoTwoGroupsWithoutConflicts(
    int total_students, int total_relationships, int relationships[][2]
) {
    DisjointSetUnionWithGroups student_groups;
    initializeDisjointSetUnion(&student_groups, total_students);

    for (int i = 0; i < total_relationships; i++) {
        int a = relationships[i][0];
        int b = relationships[i][1];

        if (!uniteTwoSetsWithBipartitionCheck(&student_groups, a, b)) {
            return 0;
        }
    }

    return 1;
}

void readInputDataFromStandardInput(
    int* total_students,
    int* total_relationships,
    int relationships[][2]
) {
    scanf("%d %d", total_students, total_relationships);
    for (int i = 0; i < *total_relationships; i++) {
        scanf("%d %d", &relationships[i][0], &relationships[i][1]);
        relationships[i][0]--;
        relationships[i][1]--;
    }
}

int main() {
    int total_students;
    int total_relationships;

    int relationships[MAXIMUM_NUMBER_OF_STUDENTS_IN_THE_CLASS *
                      MAXIMUM_NUMBER_OF_STUDENTS_IN_THE_CLASS][2];

    readInputDataFromStandardInput(
        &total_students,
        &total_relationships,
        relationships
    );

    int possible = canDivideStudentsIntoTwoGroupsWithoutConflicts(
        total_students,
        total_relationships,
        relationships
    );

    if (possible) {
        printf("YES\n");
    } else {
        printf("NO\n");
    }

    return 0;
}
