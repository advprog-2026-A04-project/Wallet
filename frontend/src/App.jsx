import './App.css'
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';

function MarkTopUpSuccess() {
    return (
        <div>
            <h1>Mark top-up request as successful</h1>
            <form>
                <label htmlFor="request-id">Top-up request id:</label>
                <input type='number' id='request-id' required /><br />
                <input type='submit' />
            </form>
        </div>
    )
}

function MarkTopUpFailed() {
    return (
        <div>
            <h1>Mark top-up request as failed</h1>
            <form>
                <label htmlFor="request-id">Top-up request id:</label>
                <input type='number' id='request-id' required /><br />
                <input type='submit' />
            </form>
        </div>
    )
}

function App() {
    return (
        <BrowserRouter>
            <nav>
                <p><Link to="/mark-success">Mark top-up as successful</Link></p>
                <p><Link to="/mark-failed">Mark top-up as failed</Link></p>
            </nav>

            <Routes>
                <Route path="/mark-success" element={<MarkTopUpSuccess />} />
                <Route path="/mark-failed" element={<MarkTopUpFailed />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App
