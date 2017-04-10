/*
 * Created by Aleksandr Smilyanskiy
 * Date: 10.04.17 11:02
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;

/**
 * Created by: Aleksandr
 * Date: 10.04.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public interface Request {

  StringBinding statusProperty();

  DoubleBinding progressProperty();
}
