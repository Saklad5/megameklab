/*
 * MegaMekLab
 * Copyright (C) 2019 The MegaMek Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package megameklab.com.ui.view.listeners;

import megamek.common.Engine;
import megamek.common.verifier.TestSupportVehicle;

/**
 * Listener for views used by support vehicle construction.
 */
public interface SVBuildListener extends BuildListener {
    void tonnageChanged(double tonnage);
    void typeChanged(TestSupportVehicle.SVType type);
    void structuralTechRatingChanged(int techRating);
    void engineChanged(Engine engine);
    void engineTechRatingChanged(int techRating);
}
