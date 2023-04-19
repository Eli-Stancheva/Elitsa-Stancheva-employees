package sirma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Task {
    public static void main(String[] args) throws IOException {
        String path = "D:\\src\\sirma\\CSVfile.txt";
        List<String> listFromCSV = Files.readAllLines(Path.of(path));
        Map<Integer, List<Employee>> projectEmployeesMap = new LinkedHashMap<>();

        for (String cslLine : listFromCSV) {
            String[] infoEmployee = cslLine.split(", ");

            int projectId = Integer.parseInt(infoEmployee[1]);
            LocalDate dateTo = infoEmployee[3].equals("NULL") ? LocalDate.now() : LocalDate.parse(infoEmployee[3]);

            Employee employee = new Employee(Integer.parseInt(infoEmployee[0]), projectId, LocalDate.parse(infoEmployee[2]), dateTo);

            if (!projectEmployeesMap.containsKey(projectId)) {
                List<Employee> employeeList = new ArrayList<>();
                employeeList.add(employee);
                projectEmployeesMap.put(projectId, employeeList);
            } else {
                projectEmployeesMap.get(projectId).add(employee);
            }
        }

        long maxDays = 0;
        int currId = 0;
        int nextId = 0;
        int projectsCount = 0;
        for (Map.Entry<Integer, List<Employee>> entry : projectEmployeesMap.entrySet()) {
            List<Employee> employees = entry.getValue();

            for (int i = 0; i < employees.size(); i++) {
                for (int j = i + 1; j < employees.size(); j++) {
                    Employee current = employees.get(i);
                    Employee next = employees.get(j);

                    LocalDate startDate = getStartDate(current, next);
                    LocalDate endDate = getEndDate(current, next);

                    if (startDate.isAfter(endDate)) {
                        continue;
                    }

                    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

                    if (daysBetween > maxDays) {
                        maxDays = daysBetween;
                        currId = current.getId();
                        nextId = next.getId();
                    }
                }
            }

            boolean foundCurr = false;
            boolean foundNext = false;
            for (Employee e : entry.getValue()) {
                if (e.getId() == currId) {
                    foundCurr = true;
                }

                if (e.getId() == nextId) {
                    foundNext = true;
                }
            }

            if (foundCurr && foundNext) {
                projectsCount++;
            }

        }
        System.out.println(currId + " " + nextId + " " + projectsCount);
    }

    private static LocalDate getStartDate(Employee e1, Employee e2) {
        return e1.getDateFrom().isAfter(e2.getDateFrom()) ? e1.getDateFrom() : e2.getDateFrom();
    }

    private static LocalDate getEndDate(Employee e1, Employee e2) {
        return e1.getDateTo().isBefore(e2.getDateTo()) ? e1.getDateTo() : e2.getDateTo();
    }
}
