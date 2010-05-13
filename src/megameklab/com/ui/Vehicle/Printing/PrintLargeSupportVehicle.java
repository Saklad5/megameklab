/*
 * MegaMekLab - Copyright (C) 2009
 *
 * Original author - jtighe (torren@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */

package megameklab.com.ui.Vehicle.Printing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PrintQuality;

import megamek.common.Engine;
import megamek.common.EquipmentType;
import megamek.common.LargeSupportTank;
import megamek.common.Pilot;
import megamek.common.Tank;
import megamek.common.TechConstants;
import megameklab.com.util.ImageHelper;
import megameklab.com.util.UnitUtil;

public class PrintLargeSupportVehicle implements Printable {

    private LargeSupportTank largesupportank = null;
    private LargeSupportTank largesupportank2 = null;
    private ArrayList<LargeSupportTank> largesupportankList;
    private int secondPageMargin = 373; // How far down the text should be
    private boolean singlePrint = false;
    PrinterJob masterPrintJob;

    // printed for a second vehicle.

    public PrintLargeSupportVehicle(ArrayList<LargeSupportTank> list, boolean singlePrint, PrinterJob masterPrintJob) {
        largesupportankList = list;
        this.singlePrint = singlePrint;
        this.masterPrintJob = masterPrintJob;
        /*
         * if (awtImage != null) { System.out.println("Width: " +
         * awtImage.getWidth(null)); System.out.println("Height: " +
         * awtImage.getHeight(null)); }
         */
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        // f.setPaper(this.paper);
        printImage(g2d, pageFormat);
        return Printable.PAGE_EXISTS;
    }

    public void printImage(Graphics2D g2d, PageFormat pageFormat) {
        if (g2d == null) {
            return;
        }

        System.gc();

        g2d.drawImage(ImageHelper.getRecordSheet(largesupportank, false), 18, 18, 558, 368, null);

        if (largesupportank.getOInternal(LargeSupportTank.LOC_TURRET) > 0) {
            g2d.drawImage(ImageHelper.getTurretImage(), 441, 173, 77, 96, null);
            g2d.drawImage(ImageHelper.getTurretLabelImage(), 297, 248, 34, 11, null);
        }

        if (largesupportank2 == null) {
            g2d.drawImage(ImageHelper.getTableImage(largesupportank), 18, 18 + secondPageMargin, 558, 368, null);
        } else {
            g2d.drawImage(ImageHelper.getRecordSheet(largesupportank2, false), 18, 18 + secondPageMargin, 558, 368, null);
            if (largesupportank2.getOInternal(LargeSupportTank.LOC_TURRET) > 0) {
                g2d.drawImage(ImageHelper.getTurretImage(), 441, 173 + secondPageMargin, 77, 96, null);
            }
        }

        printLargeSupportTankData(g2d);
        printArmor(g2d);
        printWeaponsNEquipment(g2d);

        // Armor Pips
        printFrontArmor(g2d, largesupportank.getOArmor(LargeSupportTank.LOC_FRONT), false);
        printLeftArmor(g2d, largesupportank.getOArmor(Tank.LOC_LEFT), false);
        printRightArmor(g2d, largesupportank.getOArmor(Tank.LOC_RIGHT), false);
        printRearArmor(g2d, largesupportank.getOArmor(LargeSupportTank.LOC_REAR), false);
        printTurretArmor(g2d, largesupportank.getOArmor(LargeSupportTank.LOC_TURRET), false);

        // Internal Pips
        printFrontStruct(g2d, largesupportank.getOInternal(LargeSupportTank.LOC_FRONT), false);
        printLeftStruct(g2d, largesupportank.getOInternal(Tank.LOC_LEFT), false);
        printRightStruct(g2d, largesupportank.getOInternal(Tank.LOC_RIGHT), false);
        printRearStruct(g2d, largesupportank.getOInternal(LargeSupportTank.LOC_REAR), false);

        printTurretStruct(g2d, largesupportank.getOInternal(LargeSupportTank.LOC_TURRET), false);

        if (largesupportank2 != null) {
            // Armor Pips
            printFrontArmor(g2d, largesupportank2.getOArmor(LargeSupportTank.LOC_FRONT), true);
            printLeftArmor(g2d, largesupportank2.getOArmor(Tank.LOC_LEFT), true);
            printRightArmor(g2d, largesupportank2.getOArmor(Tank.LOC_RIGHT), true);
            printRearArmor(g2d, largesupportank2.getOArmor(LargeSupportTank.LOC_REAR), true);
            printTurretArmor(g2d, largesupportank2.getOArmor(LargeSupportTank.LOC_TURRET), true);

            // Internal Pips
            printFrontStruct(g2d, largesupportank2.getOInternal(LargeSupportTank.LOC_FRONT), true);
            printLeftStruct(g2d, largesupportank2.getOInternal(Tank.LOC_LEFT), true);
            printRightStruct(g2d, largesupportank2.getOInternal(Tank.LOC_RIGHT), true);
            printRearStruct(g2d, largesupportank2.getOInternal(LargeSupportTank.LOC_REAR), true);
            printTurretStruct(g2d, largesupportank2.getOInternal(LargeSupportTank.LOC_TURRET), true);

        }

        printLargeSupportTankImage(g2d);

        g2d.scale(pageFormat.getImageableWidth(), pageFormat.getImageableHeight());

    }

    private void printLargeSupportTankData(Graphics2D g2d) {
        String largesupportankName = largesupportank.getChassis() + " " + largesupportank.getModel();

        g2d.setFont(UnitUtil.getNewFont(g2d, largesupportankName, true, 180, 10.0f));
        g2d.drawString(largesupportankName, 49, 121);

        Font font = UnitUtil.deriveFont(8.0f);
        g2d.setFont(font);

        if ((largesupportank.getCrew() != null) && !largesupportank.getCrew().getName().equalsIgnoreCase("unnamed")) {
            Pilot pilot = largesupportank.getCrew();
            g2d.drawString(pilot.getName(), 270, 120);
            g2d.drawString(String.valueOf(pilot.getGunnery()), 295, 132);
            g2d.drawString(String.valueOf(pilot.getPiloting()), 365, 132);
        }

        g2d.drawString(Integer.toString(largesupportank.getWalkMP()), 79, 144);
        g2d.drawString(Integer.toString(largesupportank.getRunMP()), 79, 155);

        g2d.drawString(largesupportank.getMovementModeAsString(), 88, 166);

        String engineName = "Fusion Engine";

        switch (largesupportank.getEngine().getEngineType()) {
            case Engine.COMBUSTION_ENGINE:
                engineName = "I.C.E.";
                break;
            case Engine.LIGHT_ENGINE:
                engineName = "Light Fusion Engine";
                break;
            case Engine.XL_ENGINE:
                engineName = "XL Fusion Engine";
                break;
            case Engine.XXL_ENGINE:
                engineName = "XXL Fusion Engine";
                break;
            case Engine.COMPACT_ENGINE:
                engineName = "Compact Fusion Engine";
                break;
            case Engine.FUEL_CELL:
                engineName = "Fuel Cell Engine";
                break;
            default:
                break;
        }

        g2d.drawString(engineName, 79, 177);

        int tonnage = (int) Math.ceil(largesupportank.getWeight());

        g2d.drawString(Integer.toString(tonnage), 177, 134);

        int nextDataLine = 153;
        int startLine = 188;
        int lineFeed = 8;

        switch (largesupportank.getTechLevel()) {

            case TechConstants.T_INTRO_BOXSET:
                ImageHelper.printCenterString(g2d, "(Intro)", font, startLine, nextDataLine);
                nextDataLine += lineFeed;
                break;
            case TechConstants.T_IS_TW_NON_BOX:
            case TechConstants.T_IS_TW_ALL:
            case TechConstants.T_CLAN_TW:
                break;
            case TechConstants.T_IS_ADVANCED:
            case TechConstants.T_CLAN_ADVANCED:
                ImageHelper.printCenterString(g2d, "(Advanced)", font, startLine, nextDataLine);
                nextDataLine += lineFeed;
                break;
            case TechConstants.T_IS_EXPERIMENTAL:
            case TechConstants.T_CLAN_EXPERIMENTAL:
                ImageHelper.printCenterString(g2d, "(Experimental)", font, startLine, nextDataLine);
                nextDataLine += lineFeed;
                break;
            case TechConstants.T_IS_UNOFFICIAL:
            case TechConstants.T_CLAN_UNOFFICIAL:
                ImageHelper.printCenterString(g2d, "(Unofficial)", font, startLine, nextDataLine);
                nextDataLine += lineFeed;
                break;
        }

        String techBase = "Inner Sphere";
        if (largesupportank.isClan()) {
            techBase = "Clan";
        }
        g2d.drawString(techBase, 177, 145);

        if ((largesupportank.getSource() != null) && (largesupportank.getSource().trim().length() > 0)) {
            String sourceFluff = "Era: ";
            font = UnitUtil.deriveFont(true, 8.0f);
            g2d.setFont(font);

            g2d.drawString(sourceFluff, 138, nextDataLine);

            font = UnitUtil.getNewFont(g2d, largesupportank.getSource(), false, 51, 8.0f);
            g2d.setFont(font);

            g2d.drawString(largesupportank.getSource(), 177, nextDataLine);

        } else {
            String yearFluff = "Year: ";
            font = UnitUtil.deriveFont(true, 8.0f);
            g2d.setFont(font);

            g2d.drawString(yearFluff, 138, nextDataLine);

            font = UnitUtil.deriveFont(8.0f);
            g2d.setFont(font);

            g2d.drawString(String.format("%1$s", largesupportank.getYear()), 177, nextDataLine);

        }

        // g2d.drawString(Integer.toString(largesupportank.getYear()), 188,
        // 155);

        // Cost/BV
        DecimalFormat myFormatter = new DecimalFormat("#,###");
        g2d.drawString(myFormatter.format(largesupportank.calculateBattleValue(true, true)), 150, 357);

        myFormatter = new DecimalFormat("#,###.##");
        g2d.drawString(myFormatter.format(largesupportank.getCost(true)) + " C-bills", 52, 357);

        if (largesupportank.hasBARArmor()) {
            font = UnitUtil.deriveFont(true, 9.0f);
            g2d.drawString("BAR: " + largesupportank.getBARRating(), 400, 64);
        }

        font = new Font("Arial", Font.BOLD, 7);
        g2d.setFont(font);
        g2d.drawString("2010", 62.5f, 374f);

        if (largesupportank2 != null) {
            printLargeSupportTank2Data(g2d);
        } else {
            g2d.drawString("2010", 62.5f, 745f);
        }
    }

    private void printLargeSupportTank2Data(Graphics2D g2d) {
        String largesupportankName = largesupportank2.getChassis().toUpperCase() + " " + largesupportank2.getModel().toUpperCase();

        g2d.setFont(UnitUtil.getNewFont(g2d, largesupportankName, true, 180, 10.0f));
        g2d.drawString(largesupportankName, 49, 494);

        Font font = UnitUtil.deriveFont(8.0f);
        g2d.setFont(font);

        if ((largesupportank2.getCrew() != null) && !largesupportank2.getCrew().getName().equalsIgnoreCase("unnamed")) {
            Pilot pilot = largesupportank2.getCrew();
            g2d.drawString(pilot.getName(), 270, 120 + secondPageMargin);
            g2d.drawString(String.valueOf(pilot.getGunnery()), 295, 132 + secondPageMargin);
            g2d.drawString(String.valueOf(pilot.getPiloting()), 365, 132 + secondPageMargin);
        }

        g2d.drawString(Integer.toString(largesupportank2.getWalkMP()), 79, 515);
        g2d.drawString(Integer.toString(largesupportank2.getRunMP()), 79, 526);

        g2d.drawString(largesupportank2.getMovementModeAsString(), 88, 537);

        String engineName = "Fusion Engine";

        switch (largesupportank2.getEngine().getEngineType()) {
            case Engine.COMBUSTION_ENGINE:
                engineName = "I.C.E.";
                break;
            case Engine.LIGHT_ENGINE:
                engineName = "Light Fusion Engine";
                break;
            case Engine.XL_ENGINE:
                engineName = "XL Fusion Engine";
                break;
            case Engine.XXL_ENGINE:
                engineName = "XXL Fusion Engine";
                break;
            case Engine.COMPACT_ENGINE:
                engineName = "Compact Fusion Engine";
                break;
            case Engine.FUEL_CELL:
                engineName = "Fuel Cell";
                break;
            default:
                break;
        }

        g2d.drawString(engineName, 79, 548);

        int tonnage = (int) Math.ceil(largesupportank2.getWeight());

        g2d.drawString(Integer.toString(tonnage), 177, 505);

        int nextDataLine = 155;
        int startLine = 188;
        int lineFeed = 8;

        switch (largesupportank2.getTechLevel()) {

            case TechConstants.T_INTRO_BOXSET:
                ImageHelper.printCenterString(g2d, "(Intro)", font, startLine, nextDataLine);
                nextDataLine += lineFeed;
                break;
            case TechConstants.T_IS_TW_NON_BOX:
            case TechConstants.T_IS_TW_ALL:
            case TechConstants.T_CLAN_TW:
                break;
            case TechConstants.T_IS_ADVANCED:
            case TechConstants.T_CLAN_ADVANCED:
                ImageHelper.printCenterString(g2d, "(Advanced)", font, startLine, nextDataLine);
                nextDataLine += lineFeed;
                break;
            case TechConstants.T_IS_EXPERIMENTAL:
            case TechConstants.T_CLAN_EXPERIMENTAL:
                ImageHelper.printCenterString(g2d, "(Experimental)", font, startLine, nextDataLine);
                nextDataLine += lineFeed;
                break;
            case TechConstants.T_IS_UNOFFICIAL:
            case TechConstants.T_CLAN_UNOFFICIAL:
                ImageHelper.printCenterString(g2d, "(Unofficial)", font, startLine, nextDataLine);
                nextDataLine += lineFeed;
                break;
        }

        String techBase = "Inner Sphere";
        if (largesupportank2.isClan()) {
            techBase = "Clan";
        }
        g2d.drawString(techBase, 177, 145);

        if ((largesupportank2.getSource() != null) && (largesupportank2.getSource().trim().length() > 0)) {
            String sourceFluff = "Era: ";
            font = UnitUtil.deriveFont(true, 8.0f);
            g2d.setFont(font);

            g2d.drawString(sourceFluff, 138, nextDataLine);

            font = UnitUtil.getNewFont(g2d, largesupportank2.getSource(), false, 51, 8.0f);
            g2d.setFont(font);

            g2d.drawString(largesupportank2.getSource(), 177, nextDataLine);

        } else {
            String yearFluff = "Year: ";
            font = UnitUtil.deriveFont(true, 8.0f);
            g2d.setFont(font);

            g2d.drawString(yearFluff, 138, nextDataLine);

            font = UnitUtil.deriveFont(8.0f);
            g2d.setFont(font);

            g2d.drawString(String.format("%1$s", largesupportank2.getYear()), 177, nextDataLine);

        }

        // Cost/BV
        DecimalFormat myFormatter = new DecimalFormat("#,###");
        g2d.drawString(myFormatter.format(largesupportank2.calculateBattleValue(true, true)), 150, 728);

        myFormatter = new DecimalFormat("#,###.##");
        g2d.drawString(myFormatter.format(largesupportank2.getCost(true)) + " C-bills", 52, 728);

        if (largesupportank.hasBARArmor()) {
            font = UnitUtil.deriveFont(true, 9.0f);
            g2d.drawString("BAR: " + largesupportank.getBARRating(), 400, 64 + secondPageMargin);
        }

        font = new Font("Arial", Font.BOLD, 7);
        g2d.setFont(font);
        g2d.drawString("2010", 105f, 745.5f);
    }

    private void printArmor(Graphics2D g2d) {

        // Armor
        Font font = UnitUtil.deriveFont(true, 9.0f);
        g2d.setFont(font);

        if ((largesupportank.getArmorType() == EquipmentType.T_ARMOR_STEALTH) || (largesupportank.getArmorType() == EquipmentType.T_ARMOR_REACTIVE) || (largesupportank.getArmorType() == EquipmentType.T_ARMOR_REFLECTIVE) || (largesupportank.getArmorType() == EquipmentType.T_ARMOR_HARDENED)) {
            font = UnitUtil.deriveFont(true, 11.0f);
            g2d.setFont(font);
            g2d.drawString(EquipmentType.getArmorTypeName(largesupportank.getArmorType()), 463, 48);
            font = UnitUtil.deriveFont(true, 9.0f);
            g2d.setFont(font);
        }
        g2d.drawString("(" + Integer.toString(largesupportank.getArmor(LargeSupportTank.LOC_FRONT)) + ")", 467, 64);

        g2d.drawString("(" + Integer.toString(largesupportank.getArmor(Tank.LOC_RIGHT)) + ")", 559, 230);

        g2d.drawString("(" + largesupportank.getArmor(Tank.LOC_LEFT) + ")", 384, 175);

        g2d.drawString("(" + Integer.toString(largesupportank.getArmor(LargeSupportTank.LOC_REAR)) + ")", 467, 342);

        if (largesupportank.getOInternal(LargeSupportTank.LOC_TURRET) > 0) {
            g2d.drawString("(" + Integer.toString(largesupportank.getArmor(LargeSupportTank.LOC_TURRET)) + ")", 455, 186);
        }

        if (largesupportank2 != null) {
            if ((largesupportank2.getArmorType() == EquipmentType.T_ARMOR_STEALTH) || (largesupportank2.getArmorType() == EquipmentType.T_ARMOR_REACTIVE) || (largesupportank2.getArmorType() == EquipmentType.T_ARMOR_REFLECTIVE) || (largesupportank2.getArmorType() == EquipmentType.T_ARMOR_HARDENED)) {
                font = UnitUtil.deriveFont(true, 11.0f);
                g2d.setFont(font);
                g2d.drawString(EquipmentType.getArmorTypeName(largesupportank2.getArmorType()), 463, 48 + secondPageMargin);
                font = UnitUtil.deriveFont(true, 9.0f);
                g2d.setFont(font);
            }
            g2d.drawString("(" + Integer.toString(largesupportank2.getArmor(LargeSupportTank.LOC_FRONT)) + ")", 467, 64 + secondPageMargin);

            g2d.drawString("(" + Integer.toString(largesupportank2.getArmor(Tank.LOC_RIGHT)) + ")", 559, 230 + secondPageMargin);

            g2d.drawString("(" + largesupportank2.getArmor(Tank.LOC_LEFT) + ")", 384, 175 + secondPageMargin);

            g2d.drawString("(" + Integer.toString(largesupportank2.getArmor(LargeSupportTank.LOC_REAR)) + ")", 467, 342 + secondPageMargin);

            if (largesupportank2.getOInternal(LargeSupportTank.LOC_TURRET) > 0) {
                g2d.drawString("(" + Integer.toString(largesupportank2.getArmor(LargeSupportTank.LOC_TURRET)) + ")", 455, 186 + secondPageMargin);
            }

        }
    }

    private void printWeaponsNEquipment(Graphics2D g2d) {

        ImageHelper.printLargeSupportTankWeaponsNEquipment(largesupportank, g2d);

        if (largesupportank2 != null) {
            ImageHelper.printLargeSupportTankWeaponsNEquipment(largesupportank2, g2d, secondPageMargin);
        }

    }

    public void print() {

        try {
            for (int pos = 0; pos < largesupportankList.size(); pos++) {
                PrinterJob pj = PrinterJob.getPrinterJob();

                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();

                aset.add(PrintQuality.HIGH);

                PageFormat pageFormat = new PageFormat();
                pageFormat = pj.getPageFormat(null);

                Paper p = pageFormat.getPaper();
                p.setImageableArea(0, 0, p.getWidth(), p.getHeight());
                pageFormat.setPaper(p);

                pj.setPrintable(this, pageFormat);

                largesupportank = largesupportankList.get(pos);
                pj.setJobName(largesupportank.getChassis() + " " + largesupportank.getModel());

                if (!singlePrint && (pos + 1 < largesupportankList.size())) {
                    largesupportank2 = largesupportankList.get(++pos);
                } else {
                    largesupportank2 = null;
                }

                try {
                    pj.print(aset);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    System.gc();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void printExtraFrontArmor(Graphics2D g2d, int totalArmor, boolean secondImage) {
        float[] topColumn =
            { 442, 90 };
        float[] middleColumn =
            { 498, 125 };
        float[] bottomColumn =
            { 493, 136.2f };
        float[] pipShift =
            { 6, 6 };

        float[][] extraArmor =
            {
                { 429, 90 },
                { 429, 97 },
                { 518, 90 },
                { 518, 97 },
                { 436f, 93.5f },
                { 436f, 100.5f },
                { 513f, 93.5f },
                { 513f, 100.5f },
                { 513f, 107.5f },
                { 436f, 107.5f } };

        if (secondImage) {
            topColumn[1] += secondPageMargin;
            middleColumn[1] += secondPageMargin;
            bottomColumn[1] += secondPageMargin;

            for (int pos = 0; pos < extraArmor.length; pos++) {
                extraArmor[pos][1] += secondPageMargin;
            }
        }

        if (totalArmor < 1) {
            return;
        }

        Vector<float[]> pipPlotter = new Vector<float[]>(20);
        for (int pos = 1; pos <= 72; pos++) {
            pipPlotter.add(new float[]
                { topColumn[0], topColumn[1] });
            topColumn[0] += pipShift[0];
            if (pos % 12 == 0) {
                topColumn[1] += pipShift[1];
                pipShift[0] *= -1;
                topColumn[0] += pipShift[0];
            }
        }

        pipShift[0] *= -1;
        for (int pos = 1; pos <= 16; pos++) {
            pipPlotter.add(new float[]
                { middleColumn[0], middleColumn[1] });
            middleColumn[0] += pipShift[0];
            if (pos % 8 == 0) {
                middleColumn[1] += pipShift[1];
                pipShift[0] *= -1;
                middleColumn[0] += pipShift[0];
            }
        }

        for (int pos = 1; pos <= 18; pos++) {
            pipPlotter.add(new float[]
                { bottomColumn[0], bottomColumn[1] });
            bottomColumn[0] += pipShift[0];
            if (pos % 6 == 0) {
                bottomColumn[1] += pipShift[1] - 0.5f;
                pipShift[0] *= -1;
                bottomColumn[0] += pipShift[0];
            }
        }

        for (int pos = 0; pos < 10; pos++) {
            pipPlotter.add(new float[]
                { extraArmor[pos][0], extraArmor[pos][1] });
        }
        printArmorPoints(g2d, pipPlotter, totalArmor);
    }

    private void printFrontArmor(Graphics2D g2d, int totalArmor, boolean secondImage) {
        float[] topColumn =
            { 444, 90 };
        float[] middleColumn =
            { 500, 125 };
        float[] bottomColumn =
            { 493, 132 };
        float[] pipShift =
            { 7, 7 };

        float[][] extraArmor =
            {
                { 430, 90 },
                { 430, 97 },
                { 519, 90 },
                { 519, 97 },
                { 437f, 93.5f },
                { 437f, 100.5f },
                { 513f, 93.5f },
                { 513f, 100.5f },
                { 513f, 107.5f },
                { 437f, 107.5f } };

        if (secondImage) {
            topColumn[1] += secondPageMargin;
            middleColumn[1] += secondPageMargin;
            bottomColumn[1] += secondPageMargin;

            for (int pos = 0; pos < extraArmor.length; pos++) {
                extraArmor[pos][1] += secondPageMargin;
            }
        }

        if (totalArmor < 1) {
            return;
        }

        if (totalArmor > 86) {
            printExtraFrontArmor(g2d, totalArmor, secondImage);
            return;
        }

        Vector<float[]> pipPlotter = new Vector<float[]>(20);
        for (int pos = 1; pos <= 50; pos++) {
            pipPlotter.add(new float[]
                { topColumn[0], topColumn[1] });
            topColumn[0] += pipShift[0];
            if (pos % 10 == 0) {
                topColumn[1] += pipShift[1];
                pipShift[0] *= -1;
                topColumn[0] += pipShift[0];
            }
        }

        for (int pos = 1; pos <= 8; pos++) {
            pipPlotter.add(new float[]
                { middleColumn[0], middleColumn[1] });
            middleColumn[0] += pipShift[0];
        }

        for (int pos = 1; pos <= 18; pos++) {
            pipPlotter.add(new float[]
                { bottomColumn[0], bottomColumn[1] });
            bottomColumn[0] += pipShift[0];
            if (pos % 6 == 0) {
                bottomColumn[1] += pipShift[1];
                pipShift[0] *= -1;
                bottomColumn[0] += pipShift[0];
            }
        }

        for (int pos = 1; pos <= 10; pos++) {
            pipPlotter.add(new float[]
                { extraArmor[pos - 1][0], extraArmor[pos - 1][1] });
        }

        printArmorPoints(g2d, pipPlotter, totalArmor);
    }

    private void printRearArmor(Graphics2D g2d, int totalArmor, boolean secondImage) {

        if (totalArmor > 51) {
            printExtraRearArmor(g2d, totalArmor, secondImage);
            return;
        }

        int[] topColumn = new int[]
            { 422, 291 };
        int[] middleColumn = new int[]
            { 422, 298 };
        int[] bottomColumn = new int[]
            { 422, 305 };
        int[] pipShift = new int[]
            { 7, 7 };

        if (secondImage) {
            topColumn[1] += secondPageMargin;
            middleColumn[1] += secondPageMargin;
            bottomColumn[1] += secondPageMargin;
        }

        int pips = 17;

        Vector<float[]> pipPlotter = new Vector<float[]>(20);
        for (int pos = 1; pos <= pips; pos++) {
            pipPlotter.add(new float[]
                { topColumn[0], topColumn[1] });
            topColumn[0] += pipShift[0];
        }

        pips = 17;
        for (int pos = 1; pos <= pips; pos++) {
            pipPlotter.add(new float[]
                { middleColumn[0], middleColumn[1] });
            middleColumn[0] += pipShift[0];
        }

        pips = 17;
        for (int pos = 1; pos <= pips; pos++) {
            pipPlotter.add(new float[]
                { bottomColumn[0], bottomColumn[1] });
            bottomColumn[0] += pipShift[0];
        }
        printArmorPoints(g2d, pipPlotter, totalArmor);

    }

    private void printExtraRearArmor(Graphics2D g2d, int totalArmor, boolean secondImage) {
        int[] topColumn = new int[]
            { 420, 291 };
        int[] middleColumn = new int[]
            { 420, 298 };
        int[] bottomColumn = new int[]
            { 420, 305 };
        int[] pipShift = new int[]
            { 5, 5 };

        if (secondImage) {
            topColumn[1] += secondPageMargin;
            middleColumn[1] += secondPageMargin;
            bottomColumn[1] += secondPageMargin;
        }

        int pips = 24;

        Vector<float[]> pipPlotter = new Vector<float[]>(20);
        for (int pos = 1; pos <= pips; pos++) {
            pipPlotter.add(new float[]
                { topColumn[0], topColumn[1] });
            topColumn[0] += pipShift[0];
        }

        pips = 24;
        for (int pos = 1; pos <= pips; pos++) {
            pipPlotter.add(new float[]
                { middleColumn[0], middleColumn[1] });
            middleColumn[0] += pipShift[0];
        }

        pips = 24;
        for (int pos = 1; pos <= pips; pos++) {
            pipPlotter.add(new float[]
                { bottomColumn[0], bottomColumn[1] });
            bottomColumn[0] += pipShift[0];
        }

        pipPlotter.add(new float[]
            { 482, 312 });

        printArmorPoints(g2d, pipPlotter, totalArmor, 7.0f);

    }

    private void printTurretArmor(Graphics2D g2d, int totalArmor, boolean secondImage) {

        if (totalArmor < 1) {
            return;
        }

        if (totalArmor > 52) {
            printExtraTurretArmor(g2d, totalArmor, secondImage);
            return;
        }

        float[] topColumn = new float[]
            { 458f, 233f };
        float[] bottomColumn = new float[]
            { 451.5f, 240f };
        float[] pipShift = new float[]
            { 6f, 6f };
        // float fontSize = 8.0f;

        if (secondImage) {
            topColumn[1] += secondPageMargin;
            bottomColumn[1] += secondPageMargin;
        }

        int pips = 7;

        Vector<float[]> pipPlotter = new Vector<float[]>(20);
        for (int pos = 1; pos <= pips; pos++) {
            pipPlotter.add(new float[]
                { topColumn[0], topColumn[1] });
            topColumn[0] += pipShift[0];
        }

        for (int pos = 1; pos <= 45; pos++) {
            pipPlotter.add(new float[]
                { bottomColumn[0], bottomColumn[1] });
            bottomColumn[0] += pipShift[0];
            if (pos % 9 == 0) {
                bottomColumn[1] += pipShift[1];
                pipShift[0] *= -1;
                bottomColumn[0] += pipShift[0];
            }
        }
        printArmorPoints(g2d, pipPlotter, totalArmor);// , fontSize);
    }

    private void printExtraTurretArmor(Graphics2D g2d, int totalArmor, boolean secondImage) {

        if (totalArmor < 1) {
            return;
        }

        float[] topColumn = new float[]
            { 456.5f, 232f };
        float[] bottomColumn = new float[]
            { 453.5f, 238f };
        float[] pipShift = new float[]
            { 4.5f, 4.5f };
        float fontSize = 5.5f;

        if (secondImage) {
            topColumn[1] += secondPageMargin;
            bottomColumn[1] += secondPageMargin;
        }

        int pips = 9;

        Vector<float[]> pipPlotter = new Vector<float[]>(20);
        for (int pos = 1; pos <= pips; pos++) {
            pipPlotter.add(new float[]
                { topColumn[0], topColumn[1] });
            topColumn[0] += pipShift[0];
        }

        for (int pos = 1; pos <= 70; pos++) {
            pipPlotter.add(new float[]
                { bottomColumn[0], bottomColumn[1] });
            bottomColumn[0] += pipShift[0];
            if (pos % 11 == 0) {
                bottomColumn[1] += pipShift[1];
                pipShift[0] *= -1;
                bottomColumn[0] += pipShift[0];
            }
        }
        printArmorPoints(g2d, pipPlotter, totalArmor, fontSize);
    }

    private void printLeftArmor(Graphics2D g2d, int totalArmor, boolean secondImage) {
        float[] topColumn = new float[]
            { 418.5f, 92.5f };
        float[] pipShift = new float[]
            { 7, 7 };

        if (secondImage) {
            topColumn[1] += secondPageMargin;
        }

        Vector<float[]> pipPlotter = new Vector<float[]>(20);
        for (int pos = 1; pos <= 90; pos++) {
            pipPlotter.add(new float[]
                { topColumn[0], topColumn[1] });

            switch (pos) {
                case 1:
                case 2:
                    topColumn[1] += pipShift[1];
                    break;
                case 4:
                case 6:
                case 9:
                case 12:
                case 21:
                case 27:
                case 46:
                case 49:
                case 52:
                case 70:
                case 74:
                case 85:
                case 88:
                    topColumn[1] += pipShift[1];
                    pipShift[0] *= -1;
                    break;
                case 78:
                    topColumn[1] += pipShift[1];
                    pipShift[0] *= -1;
                    topColumn[0] -= 1.3;
                    break;
                case 82:
                    topColumn[1] += pipShift[1];
                    pipShift[0] *= -1;
                    topColumn[0] += 1.3;
                    break;
                case 43:
                    topColumn[1] += pipShift[1];
                    pipShift[0] *= -1;
                    topColumn[0] += pipShift[0];
                    break;
                case 39:
                    topColumn[1] += pipShift[1];
                    pipShift[0] = Math.abs(pipShift[0]);
                    topColumn[0] -= pipShift[0] * 5.2;
                    break;
                case 55:
                    topColumn[1] += pipShift[1];
                    topColumn[0] -= pipShift[0] * 2.2;
                    break;
                case 58:
                case 60:
                case 62:
                case 64:
                    topColumn[1] += pipShift[1];
                    topColumn[0] -= pipShift[0] * 1.25;
                    break;
                case 66:
                    topColumn[1] += pipShift[1] * 1.3;
                    topColumn[0] -= pipShift[0];
                    break;
                case 16:
                case 33:
                    topColumn[1] += pipShift[1];
                    pipShift[0] *= -1;
                    topColumn[0] -= pipShift[0] * .5;
                    break;
                case 79:
                case 81:
                case 87:
                    topColumn[0] += pipShift[0] * .85;
                    break;
                default:
                    topColumn[0] += pipShift[0];
                    break;
            }

        }
        printArmorPoints(g2d, pipPlotter, totalArmor);
    }

    private void printRightArmor(Graphics2D g2d, int totalArmor, boolean secondImage) {
        float[] topColumn = new float[]
            { 530f, 92 };
        float[] pipShift = new float[]
            { 7, 7 };

        if (secondImage) {
            topColumn[1] += secondPageMargin;
        }

        Vector<float[]> pipPlotter = new Vector<float[]>(20);
        for (int pos = 1; pos <= 90; pos++) {
            pipPlotter.add(new float[]
                { topColumn[0], topColumn[1] });

            switch (pos) {
                case 1:
                case 2:
                    topColumn[1] += pipShift[1];
                    break;
                case 4:
                case 6:
                case 9:
                case 12:
                case 16:
                case 21:
                case 27:
                case 33:
                case 46:
                case 49:
                case 52:
                case 70:
                case 74:
                    // case 78:
                case 85:
                case 82:
                case 88:
                    topColumn[1] += pipShift[1];
                    pipShift[0] *= -1;
                    break;
                case 78:
                    topColumn[1] += pipShift[1];
                    topColumn[0] += 1.5f;
                    pipShift[0] *= -.9f;
                    break;
                case 43:
                    topColumn[1] += pipShift[1];
                    pipShift[0] *= -1;
                    topColumn[0] -= pipShift[0];
                    break;
                case 39:
                    topColumn[1] += pipShift[1];
                    pipShift[0] = Math.abs(pipShift[0]);
                    topColumn[0] += pipShift[0] * 5.5;
                    break;
                case 55:
                    topColumn[1] += pipShift[1];
                    topColumn[0] += pipShift[0] * 2.2;
                    break;
                case 58:
                case 60:
                case 62:
                case 64:
                    topColumn[1] += pipShift[1];
                    topColumn[0] += pipShift[0] * 1.25;
                    break;
                case 66:
                    topColumn[1] += pipShift[1] * 1.25;
                    topColumn[0] += pipShift[0];
                    break;
                case 89:
                    topColumn[0] -= pipShift[0] * .9;
                    break;
                default:
                    topColumn[0] -= pipShift[0];
                    break;
            }
        }
        printArmorPoints(g2d, pipPlotter, totalArmor);
    }

    private void printFrontStruct(Graphics2D g2d, int totalArmor, boolean secondImage) {
        int[] topColumn = new int[]
            { 462, 148 };
        int[] bottomColumn = new int[]
            { 462, 156 };
        int[] pipShift = new int[]
            { 7, 7 };

        if (secondImage) {
            topColumn[1] += secondPageMargin;
            bottomColumn[1] += secondPageMargin;
        }

        int pips = Math.min(5, totalArmor);

        totalArmor -= pips;
        topColumn[0] += pipShift[0] * ((5 - pips) / 2);
        for (int pos = 1; pos <= pips; pos++) {
            ImageHelper.drawTankISPip(g2d, topColumn[0], topColumn[1]);
            topColumn[0] += pipShift[0];
        }

        bottomColumn[0] += pipShift[0] * ((5 - totalArmor) / 2);
        for (int pos = 1; pos <= totalArmor; pos++) {
            ImageHelper.drawTankISPip(g2d, bottomColumn[0], bottomColumn[1]);
            bottomColumn[0] += pipShift[0];
        }
    }

    private void printTurretStruct(Graphics2D g2d, int totalArmor, boolean secondImage) {
        int[] topColumn = new int[]
            { 462, 210 };
        int[] bottomColumn = new int[]
            { 462, 218 };
        int[] pipShift = new int[]
            { 7, 7 };

        if (totalArmor < 1) {
            return;
        }

        if (secondImage) {
            topColumn[1] += secondPageMargin;
            bottomColumn[1] += secondPageMargin;
        }

        int pips = Math.min(5, totalArmor);

        totalArmor -= pips;
        topColumn[0] += pipShift[0] * ((5 - pips) / 2);
        for (int pos = 1; pos <= pips; pos++) {
            ImageHelper.drawTankISPip(g2d, topColumn[0], topColumn[1]);
            topColumn[0] += pipShift[0];
        }

        bottomColumn[0] += pipShift[0] * ((5 - totalArmor) / 2);
        for (int pos = 1; pos <= totalArmor; pos++) {
            ImageHelper.drawTankISPip(g2d, bottomColumn[0], bottomColumn[1]);
            bottomColumn[0] += pipShift[0];
        }
    }

    private void printLeftStruct(Graphics2D g2d, int totalArmor, boolean secondImage) {
        int[] column = new int[]
            { 444, 164 };
        int[] pipShift = new int[]
            { 2, 7 };

        if (secondImage) {
            column[1] += secondPageMargin;
        }

        for (int pos = 1; pos <= totalArmor; pos++) {
            ImageHelper.drawTankISPip(g2d, column[0], column[1]);
            column[0] -= pipShift[0];
            column[1] += pipShift[1];
        }
    }

    private void printRightStruct(Graphics2D g2d, int totalArmor, boolean secondImage) {
        int[] column = new int[]
            { 505, 164 };
        int[] pipShift = new int[]
            { 2, 7 };

        if (secondImage) {
            column[1] += secondPageMargin;
        }

        for (int pos = 1; pos <= totalArmor; pos++) {
            ImageHelper.drawTankISPip(g2d, column[0], column[1]);
            column[0] += pipShift[0];
            column[1] += pipShift[1];
        }
    }

    private void printRearStruct(Graphics2D g2d, int totalArmor, boolean secondImage) {
        int[] column = new int[]
            { 445, 275 };
        int[] pipShift = new int[]
            { 7, 7 };

        if (secondImage) {
            column[1] += secondPageMargin;
        }

        column[0] += pipShift[0] * ((10 - totalArmor) / 2);
        for (int pos = 1; pos <= totalArmor; pos++) {
            ImageHelper.drawTankISPip(g2d, column[0], column[1]);
            column[0] += pipShift[0];
        }
    }

    private void printArmorPoints(Graphics2D g2d, Vector<float[]> pipPoints, float totalArmor) {
        printArmorPoints(g2d, pipPoints, totalArmor, 8.0f);
    }

    private void printArmorPoints(Graphics2D g2d, Vector<float[]> pipPoints, float totalArmor, float fontSize) {
        pipPoints.trimToSize();
        float pipSpace = pipPoints.size() / totalArmor;
        for (float pos = 0; pos < pipPoints.size(); pos += pipSpace) {
            int currentPip = (int) pos;
            ImageHelper.drawTankArmorPip(g2d, pipPoints.get(currentPip)[0], pipPoints.get(currentPip)[1], fontSize);
            if (--totalArmor <= 0) {
                return;
            }
        }
    }

    private void printLargeSupportTankImage(Graphics2D g2d) {

        Image img = ImageHelper.getFluffImage(largesupportank, ImageHelper.imageVehicle);
        int width = Math.min(148, img.getWidth(null));
        int height = Math.min(99, img.getHeight(null));
        int drawingX = 235 + ((148 - width) / 2);
        int drawingY = 270 + ((99 - height) / 2);
        g2d.drawImage(img, drawingX, drawingY, width, height, Color.BLACK, null);

        if (largesupportank2 != null) {
            img = ImageHelper.getFluffImage(largesupportank2, ImageHelper.imageVehicle);
            width = Math.min(148, img.getWidth(null));
            height = Math.min(99, img.getHeight(null));
            drawingX = 235 + ((148 - width) / 2);
            drawingY = 268 + ((99 - height) / 2) + secondPageMargin;
            g2d.drawImage(img, drawingX, drawingY, width, height, Color.BLACK, null);
        }
    }

}