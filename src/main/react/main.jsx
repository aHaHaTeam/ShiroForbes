import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import "/static/styles/index.scss";
import Router from "/src/Router.jsx";
import "/static/beer/beer.min.css"
import "/static/beer/beer.min.js"
import "/static/beer/material-dynamic-colors.min.js"

import {setDefaultTheme} from "/src/components/ThemeButton.jsx";
setDefaultTheme(document.body)

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Router />
  </StrictMode>,
)
