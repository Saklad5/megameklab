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

/*
 * Thanks to Lost in space of the Solaris Sunk Works Project for the code snippet and idea.
 */

package megameklab.com.util;

import megamek.common.*;
import megameklab.com.ui.util.CritCellUtil;

import javax.swing.*;
import java.awt.*;

import static megameklab.com.ui.util.CritCellUtil.*;

public class CritListCellRenderer extends DefaultListCellRenderer {

    private JList<?> list = null;
    private final Entity unit;
    private final boolean useColor;

    public CritListCellRenderer(Entity unit, boolean useColor) {
        this.unit = unit;
        this.useColor = useColor;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean hasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
        this.list = list;

        String[] split = ((String)value).split(":");
        setText(split[0]);
        setToolTipText(null);

        CriticalSlot cs;
        if (split.length > 2) {
            int eqId = Integer.parseInt(split[2]);
            cs = new CriticalSlot(unit.getEquipment(eqId));
        } else if (split.length > 1) {
            cs = getCrit(Integer.parseInt(split[1]));
        } else if (value.equals("-Empty-")) {
            cs = null;
        } else {
            cs = getCrit(index);
        }

        if (cs != null) {
            if (cs.getType() == CriticalSlot.TYPE_SYSTEM) {
                if (useColor) {
                    setBackground(CConfig.getBackgroundColor(CConfig.CONFIG_SYSTEMS));
                    setForeground(CConfig.getForegroundColor(CConfig.CONFIG_SYSTEMS));
                }
                if (cs.isArmored()) {
                    setText(getText() + " (A)");
                }
                setText(" " + getText());
            } else if (cs.getMount() != null) {
                CritCellUtil.formatCell(this, cs.getMount(), useColor, unit, index);
                if (cs.getMount2() != null) {
                    setText(getText() + " | " + UnitUtil.getCritName(unit, cs.getMount2().getType()));
                }
            }
        } else {
            CritCellUtil.formatCell(this, null, useColor, unit, index);
        }

        int loc = getCritLocation();
        if ((cs != null) 
                && UnitUtil.isLastCrit(unit, cs, index, loc) 
                && UnitUtil.isPreviousCritEmpty(unit, cs, index, loc)) {
            setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, CritCellUtil.CRITCELL_BORDERCOLOR));
        } else if ((cs != null) && UnitUtil.isLastCrit(unit, cs, index, loc)) {
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CritCellUtil.CRITCELL_BORDERCOLOR));
        } else if ((cs != null) && UnitUtil.isPreviousCritEmpty(unit, cs, index, loc)) {
            setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, CritCellUtil.CRITCELL_BORDERCOLOR));
        } 

        return this;
    }

    @Override
    public Dimension getPreferredSize() {
        int width = CRITCELL_WIDTH;
        width = (unit instanceof Mech) ? CRITCELL_MEKWIDTH : width;
        width = (unit instanceof Tank) ? CRITCELL_VEHWIDTH : width;
        int height = Math.max(CRITCELL_MINHEIGHT, super.getPreferredSize().height + CRITCELL_ADDHEIGHT);
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension pref = getPreferredSize();
        return new Dimension(pref.width * 2, pref.height);
    }

    private CriticalSlot getCrit(int slot) {
        int location = getCritLocation();
        CriticalSlot crit = null;
        if ((slot >= 0) && (slot < unit.getNumberOfCriticals(location))) {
            crit = unit.getCritical(location, slot);
        }
        return crit;
    }

    private int getCritLocation() {
        if (unit instanceof BattleArmor){
            String[] split = list.getName().split(":");
            return Integer.parseInt(split[0]);
        } else {
            return Integer.parseInt(list.getName());
        }
    }

}