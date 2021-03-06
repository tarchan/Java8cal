/*
 * The MIT License
 *
 * Copyright 2014 tarchan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mac.tarchan;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * CalController
 *
 * @author tarchan
 */
public class CalController implements Initializable {

    private static final Logger log = Logger.getLogger(CalController.class.getName());
    private final ObjectProperty<YearMonth> month = new SimpleObjectProperty();
    @FXML
    private Label monthLabel;
    @FXML
    private GridPane dayGrid;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        month.addListener((ObservableValue<? extends YearMonth> observable, YearMonth oldValue, YearMonth newValue) -> {
            if (newValue != null) {
                updateLabel(newValue);
                updateGrid(newValue);
            }
        });
        month.set(YearMonth.now());
//        monthLabel.textProperty().bind(month.asString(Locale.ENGLISH, "%tY/%<tm%n%<tB"));
    }

    @FXML
    private void onBack(ActionEvent event) {
        month.set(month.get().minusMonths(1));
    }

    @FXML
    private void onNext(ActionEvent event) {
        month.set(month.get().plusMonths(1));
    }

    @FXML
    private void onToday(MouseEvent event) {
        month.set(YearMonth.now());
    }

    private void updateLabel(YearMonth ym) {
        JapaneseDate jp = JapaneseDate.from(ym.atEndOfMonth());
        monthLabel.setText(String.format("%s%n%s%n%s"
                , ym.format(DateTimeFormatter.ofPattern("yyyy/MM"))
                , ym.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH))
                , jp.format(DateTimeFormatter.ofPattern("Gy年", Locale.JAPANESE))
        ));
    }

    private void clearGrid() {
        dayGrid.getChildren().clear();
        DayOfWeek week = DayOfWeek.SUNDAY;
        for (int i = 0; i < 7; i++) {
            Label node = new Label(week.getDisplayName(TextStyle.SHORT, Locale.JAPANESE));
            dayGrid.add(node, i, 0);
            week = week.plus(1);
        }
    }

    private void updateGrid(YearMonth ym) {
        clearGrid();
        LocalDate date = ym.atDay(1);
        LocalDate today = LocalDate.now().plusDays(0);
        int weekCount = DayOfWeek.values().length;
        DayOfWeek week = date.getDayOfWeek();
        log.log(Level.INFO, "first day: {0}", week);
        int col = week.getValue() % weekCount;
        int row = 1;
        for (int i = 1; i <= ym.lengthOfMonth(); i++) {
            Group g = new Group();
            Label label = new Label("" + i);
            g.getChildren().add(label);
            if (ym.atDay(i).isEqual(today)) {
//                node.setStyle("-fx-background-color: pink;");
                Circle circle = new Circle(10);
                circle.setFill(Color.TRANSPARENT);
                circle.setStroke(Color.RED);
                circle.setTranslateX(6);
                circle.setTranslateY(8);
                g.getChildren().add(circle);
            }
            log.log(Level.CONFIG, String.format("[%s] %s, %s", i, col, row));
            dayGrid.add(g, col, row);
            if (++col == weekCount) {
                col = 0;
                row++;
            }
        }
    }
}
