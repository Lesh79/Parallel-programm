# Стек Трайбера

Поставленные задачи:
1. Реализовать стек Трайбера
2. Реализовать стек Трайбера с Илюминацией
3. Сравнить два стека

## Эксперимент
Эксперимент проводился на компьютере с ОС ``Windows 10`` и процессором ``11th Gen Intel(R) Core(TM) i5-11400h @ 2.70Hz``

``JDK Version: 17 ``

![Results.png](img%2FResults.png)

## Выводы
[Результаты эксперимента](https://docs.google.com/spreadsheets/d/1Bqiso1b6oJegCTrTX26EaMTh7SZ_LJYH4fwAyCYjbpE/edit?usp=sharing)

- Elimination Stack работает медленее по сравнению с Treiber Stack при большом количестве потоков и(или) итераций 
- При небольших итерациях и потоках Elimination Stack работает в несколько раз быстрее Treiber Stack