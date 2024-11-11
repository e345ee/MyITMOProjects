import subprocess

def execute_tests():
    test_cases = [
        ("itmo", "Введите ключ для поиска в словаре:\nblue"),
        ("Politech", "Введите ключ для поиска в словаре:\ngreen"),
        ("spbgu", "Введите ключ для поиска в словаре:\nmulticolor"),
        ("", "Введите ключ для поиска в словаре:\nОшибка: Ключ не найден в словаре."),
        ("tonySoprano", "Введите ключ для поиска в словаре:\nОшибка: Ключ не найден в словаре."),
        ("0" * 256, "Введите ключ для поиска в словаре:\nОшибка: Ввод превышает 255 символов."),
        ("1" * 255, "Введите ключ для поиска в словаре:\nОшибка: Ключ не найден в словаре."),
        (" ", "Введите ключ для поиска в словаре:\nОшибка: Ключ не найден в словаре.")
    ]

    total_passed = 0
    total_tests = len(test_cases)

    for idx, (input_value, expected_output) in enumerate(test_cases, start=1):
        process = subprocess.Popen(["./main"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout_data, stderr_data = process.communicate(input=input_value.encode())
        stdout_text = stdout_data.decode("utf-8").strip()
        stderr_text = stderr_data.decode("utf-8").strip()

        combined_result = f"{stdout_text}\n{stderr_text}".strip()

        print("=" * 34)
        if combined_result == expected_output:
            print(f"Тест {idx}: Успешно")
            total_passed += 1
        else:
            print(f"Тест {idx}: Провален")
            print(f"Входные данные: {repr(input_value)}")
            print(f"Ожидаемый вывод:\n{repr(expected_output)}")
            print("Полученный вывод:")
            print(f"stdout:\n{repr(stdout_text)}")
            print(f"stderr:\n{repr(stderr_text)}\n")

    print("=" * 34)
    print(f"Пройдено тестов: {total_passed} из {total_tests}")
    print(f"Успешно: {total_passed}, Провалено: {total_tests - total_passed}")

if __name__ == "__main__":
    execute_tests()