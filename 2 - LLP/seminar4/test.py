import subprocess

def execute_tests():
    
    test_cases = [
        ("-6000", "-3000"),   
        ("-4000", "-1000"),    
        ("-4500", "-1500"),
        ("-4333", "-1333"),
        ("-3000", "0"),
        ("-2000", "2236"),
        ("-2333", "1886"),
        ("0", "3000"),
        ("1000", "2500"), 
        ("1333", "2334"),
        ("2400", "1800"),
        ("4000", "1000"),
        ("6000", "0"), 
        ("8000", "2000"), 
        ("6200", "200"), 
        ("6660", "660")     
    ]

    total_passed = 0
    total_tests = len(test_cases)

    for idx, (input_value, expected_output) in enumerate(test_cases, start=1):
        
        process = subprocess.Popen(
            ["./function"],  
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )
        
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
